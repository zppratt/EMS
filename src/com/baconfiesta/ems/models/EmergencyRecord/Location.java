package com.baconfiesta.ems.models.EmergencyRecord;

import java.io.Serializable;

/**
 * A location of an emergency in the system
 * @author team_bacon_fiesta
 */
public class Location implements Serializable {

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
     * Default constructor
     * @param address the address of the emergency
     * @param state the state the emergency is in
     * @param zip the zip code of the emergency
     */
    public Location(String address, String state, int zip) {
        this.address = address;
        this.state = state;
        this.zip = zip;
    }

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