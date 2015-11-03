package com.baconfiesta.ems.models.EmergencyRecord;

public class Location {

    /**
     * The address of the emergency
     */
    private String emergencyAddress;

    /**
     * The state where the emergency is/was located
     */
    private String emergencyState;

    /**
     * The zip code where the emergency is/was located
     */
    private int emergencyZip;

    /**
     * Retrieve the address of the emergency
     * @return the address
     */
    public String getEmergencyAddress() {
        return emergencyAddress;
    }

    /**
     * Retrieve the state where the emergency is/was located
     * @return the state
     */
    public String getEmergencyState() {
        return emergencyState;
    }

    /**
     * Retrieve the zip code where the emergency is/was located
     * @return the zip code
     */
    public int getEmergencyZip() {
        return emergencyZip;
    }

}