package com.netbucket.studymate.model;

public class Student {
    String fullName;
    String email;
    String phoneNumber;
    String username;
    String about;
    String birthday;
    String gender;
    String institute;
    String role;
    String course;
    String id;
    String semOrYear;
    String profileImageUri;
    String profileEditAccess;

    public Student() {
        // Empty constructor needed
    }

    public Student(String fullName, String email, String phoneNumber, String username, String about, String birthday, String gender, String institute, String role, String course, String id, String semOrYear, String profileImageUri, String profileEditAccess) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.about = about;
        this.birthday = birthday;
        this.course = course;
        this.gender = gender;
        this.institute = institute;
        this.role = role;
        this.id = id;
        this.semOrYear = semOrYear;
        this.profileImageUri = profileImageUri;
        this.profileEditAccess = profileEditAccess;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSemOrYear() {
        return semOrYear;
    }

    public void setSemOrYear(String semOrYear) {
        this.semOrYear = semOrYear;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getProfileEditAccess() {
        return profileEditAccess;
    }

    public void setProfileEditAccess(String profileEditAccess) {
        this.profileEditAccess = profileEditAccess;
    }
}
