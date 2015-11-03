package com.baconfiesta.ems.models.EmergencyRecord;

/**
 * A location of an emergency in the system
 * @author team_bacon_fiesta
 */
public class Location {

    /**
     * The address of the emergency
     */
    private String address;

    /**
     * The state where the emergency is/was located
     */
    private String state;

    /**
     * The zip code where the emergency is/was located
     */
    private int zip;

    /**
     * Retrieve the address of the emergency
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Retrieve the state where the emergency is/was located
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Retrieve the zip code where the emergency is/was located
     * @return the zip code
     */
    public int getZip() {
        return zip;
    }

}