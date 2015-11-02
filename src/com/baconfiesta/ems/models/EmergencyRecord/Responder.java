package com.baconfiesta.ems.models.EmergencyRecord;

public class Responder {

    private String phoneNumber;
    private String address;
    private String state;
    private int zip;

    public Responder(String phoneNumber, String address, String state, int zip) {
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.state = state;
        this.zip = zip;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getState() {
        return state;
    }

    public int getZip() {
        return zip;
    }

}
