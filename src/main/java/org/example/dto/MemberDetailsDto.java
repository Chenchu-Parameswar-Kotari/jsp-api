package org.example.dto;

public class MemberDetailsDto {
    private String fullName;
    private String gender;
    private String qualification;
    private String profession;
    private String religion;
    private String caste;
    private String reservation;
    private String mobileNumber;
    // File fields will be handled separately in the controller

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }
    public String getCaste() { return caste; }
    public void setCaste(String caste) { this.caste = caste; }
    public String getReservation() { return reservation; }
    public void setReservation(String reservation) { this.reservation = reservation; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
}
