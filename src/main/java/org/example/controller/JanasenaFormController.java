package org.example.controller;

import org.example.dto.JanasenaFormDto;
import org.example.entity.JanasenaForm;
import org.example.repository.JanasenaFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/form")
public class JanasenaFormController {

    @Autowired
    private JanasenaFormRepository formRepository;

    private String saveFile(MultipartFile file, String prefix) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String folder = "uploads/";
        Files.createDirectories(Paths.get(folder));
        String filePath = folder + prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());
        return filePath;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<String> submitForm(
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
            formRepository.save(form);
            return ResponseEntity.ok("Form submitted and saved to database successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
