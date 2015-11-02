package com.baconfiesta.ems.models.EmergencyRecord;

public class Location {

    private String emergencyAddress;
    private String emergencyState;
    private int emergencyZip;
    private String emergencyCategory;

    public String getEmergencyAddress() {
        return emergencyAddress;
    }

    public String getEmergencyState() {
        return emergencyState;
    }

    public int getEmergencyZip() {
        return emergencyZip;
    }

    public String getEmergencyCategory() {
        return emergencyCategory;
    }

}
