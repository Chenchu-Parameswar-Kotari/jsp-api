package org.example.controller;

import org.example.dto.JanasenaFormDto;
import org.example.entity.JanasenaForm;
import org.example.repository.JanasenaFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/form")
public class JanasenaFormController {

    private static final Logger logger = LoggerFactory.getLogger(JanasenaFormController.class);
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB in bytes

    @Autowired
    private JanasenaFormRepository formRepository;

    private void validateFileSize(MultipartFile file, String fieldName) throws IOException {
        if (file != null && file.getSize() > MAX_FILE_SIZE) {
            throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
        }
    }

    private boolean isValidFileType(MultipartFile file) {
        if (file == null || file.isEmpty()) return true;
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("application/pdf") || contentType.equals("image/jpeg") || contentType.equals("image/png"));
    }

    private String saveFile(MultipartFile file, String prefix) throws IOException {
        if (file == null || file.isEmpty()) return null;
        validateFileSize(file, prefix);
        if (!isValidFileType(file)) throw new IOException("Invalid file type. Only PDF, JPG, and PNG are allowed.");
        String folder = "uploads/";
        Files.createDirectories(Paths.get(folder));
        String filePath = folder + prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());
        return filePath;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(413)
            .body("File too large! Maximum allowed size is 50MB per file and 100MB total request size.");
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> submitForm(
            @RequestPart("form") JanasenaFormDto formDto,
            @RequestPart(value = "memberAadharDocument", required = false) MultipartFile memberAadharDocument,
            @RequestPart(value = "memberPhoto", required = false) MultipartFile memberPhoto,
            @RequestPart(value = "nomineeAadharDocument", required = false) MultipartFile nomineeAadharDocument,
            @RequestPart(value = "nomineePhoto", required = false) MultipartFile nomineePhoto
    ) {
        try {
            JanasenaForm form = new JanasenaForm();
            form.setConstituency(formDto.getConstituency());
            form.setMandalTown(formDto.getMandalTown());
            form.setPanchayathiStreet(formDto.getPanchayathiStreet());
            form.setVillageWard(formDto.getVillageWard());
            form.setContactName1(formDto.getContactName1());
            form.setPhone1(formDto.getPhone1());
            form.setContactName2(formDto.getContactName2());
            form.setPhone2(formDto.getPhone2());
            // Member Details
            form.setMemberFullName(formDto.getMemberFullName());
            form.setMemberGender(formDto.getMemberGender());
            form.setMemberQualification(formDto.getMemberQualification());
            form.setMemberProfession(formDto.getMemberProfession());
            form.setMemberReligion(formDto.getMemberReligion());
            form.setMemberCaste(formDto.getMemberCaste());
            form.setMemberReservation(formDto.getMemberReservation());
            form.setMemberMobileNumber(formDto.getMemberMobileNumber());
            form.setMemberAadharDocumentPath(saveFile(memberAadharDocument, "member_aadhar"));
            form.setMemberPhotoPath(saveFile(memberPhoto, "member_photo"));
            // Nominee Details
            form.setNomineeFullName(formDto.getNomineeFullName());
            form.setNomineeGender(formDto.getNomineeGender());
            form.setNomineeQualification(formDto.getNomineeQualification());
            form.setNomineeProfession(formDto.getNomineeProfession());
            form.setNomineeReligion(formDto.getNomineeReligion());
            form.setNomineeCaste(formDto.getNomineeCaste());
            form.setNomineeReservation(formDto.getNomineeReservation());
            form.setNomineeMobileNumber(formDto.getNomineeMobileNumber());
            form.setNomineeAadharDocumentPath(saveFile(nomineeAadharDocument, "nominee_aadhar"));
            form.setNomineePhotoPath(saveFile(nomineePhoto, "nominee_photo"));
            JanasenaForm saved = formRepository.save(form);
            return ResponseEntity.ok("Form submitted successfully. ID: " + saved.getId());
        } catch (MaxUploadSizeExceededException e) {
            logger.error("File size exceeded", e);
            return ResponseEntity.status(413)
                    .body("File too large! Maximum allowed size is 50MB per file and 100MB total request size.");
        } catch (IOException e) {
            logger.error("File upload error", e);
            return ResponseEntity.status(400).body("File upload error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("General error", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
