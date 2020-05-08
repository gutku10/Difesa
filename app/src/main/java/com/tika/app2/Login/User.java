package com.tika.app2.Login;

import com.google.firebase.database.PropertyName;

public class User {

    private String firstName;
    private String lastName;
    private String height;
    private String weight;
    private String bloodGroup;
    private String emergencyPhone1;
    private String emergencyPhone2;
    private String age;
    private String dateOfBirth;
    private String address;
    private String email;
    private String myPhone;

    public User(){

    }

    public User(String firstName, String lastName, String height, String weight, String bloodGroup, String emergencyPhone1, String emergencyPhone2, String myPhone, String age, String dateOfBirth, String address, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.height = height;
        this.weight = weight;
        this.bloodGroup = bloodGroup;
        this.emergencyPhone1 = emergencyPhone1;
        this.emergencyPhone2 = emergencyPhone2;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
        this.myPhone = myPhone;
    }

    @PropertyName("First Name")
    public String getFirstName() {
        return firstName;
    }

    @PropertyName("First Name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @PropertyName("Last Name")
    public String getLastName() {
        return lastName;
    }

    @PropertyName("Last Name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @PropertyName("Height")
    public String getHeight() {
        return height;
    }

    @PropertyName("Height")
    public void setHeight(String height) {
        this.height = height;
    }

    @PropertyName("Weight")
    public String getWeight() {
        return weight;
    }

    @PropertyName("Weight")
    public void setWeight(String weight) {
        this.weight = weight;
    }

    @PropertyName("Blood Group")
    public String getBloodGroup() {
        return bloodGroup;
    }

    @PropertyName("Blood Group")
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    @PropertyName("Emergency Contact 1")
    public String getEmergencyPhone1() {
        return emergencyPhone1;
    }

    @PropertyName("Emergency Contact 1")
    public void setEmergencyPhone1(String emergencyPhone1) {
        this.emergencyPhone1 = emergencyPhone1;
    }

    @PropertyName("Emergency Contact 2")
    public String getEmergencyPhone2() {
        return emergencyPhone2;
    }

    @PropertyName("Emergency Contact 2")
    public void setEmergencyPhone2(String emergencyPhone2) {
        this.emergencyPhone2 = emergencyPhone2;
    }

    @PropertyName("Age")
    public String getAge() {
        return age;
    }

    @PropertyName("Age")
    public void setAge(String age) {
        this.age = age;
    }

    @PropertyName("Date of Birth")
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    @PropertyName("Date of Birth")
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @PropertyName("Address")
    public String getAddress() {
        return address;
    }

    @PropertyName("Address")
    public void setAddress(String address) {
        this.address = address;
    }

    @PropertyName("Email ID")
    public String getEmail() {
        return email;
    }

    @PropertyName("Email ID")
    public void setEmail(String email) {
        this.email = email;
    }
    
    @PropertyName("User Phone number")
    public String getMyPhone() {
        return myPhone;
    }

    @PropertyName("User Phone number")
    public void setMyPhone(String myPhone) { 
        this.myPhone = myPhone;
    }
}
