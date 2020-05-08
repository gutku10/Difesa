package com.tika.app2.Selections;

public class InfantDetails {

    private String vaccineName;
    private String vaccineDet;
    private String vaccineDose;
    private String videoCode;
    private String videodesc;
    private int vaccineImage;

    public String getVaccineName() {
        return vaccineName;
    }

    public String getVaccineDet() {
        return vaccineDet;
    }

    public String getVaccineDose() {
        return vaccineDose;
    }

    public String getVideoCode() { return videoCode;}

    public String getVideodesc() { return videodesc;}

    public int getVaccineImage() {
        return vaccineImage;
    }

    public InfantDetails(String vaccineName, String vaccineDet, String vaccineDose, int vaccineImage, String videoCode, String videdesc ){

        this.vaccineName = vaccineName;
        this.vaccineDet = vaccineDet;
        this.vaccineDose = vaccineDose;
        this.vaccineImage = vaccineImage;
        this.videoCode = videoCode;
        this.videodesc = videdesc;


    }

}
