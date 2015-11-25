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
    private String city;

    /**
     * Default constructor
     * @param address the address of the emergency
     * @param state the state the emergency is in
     * @param city the zip code of the emergency
     */
    public Location(String address, String state, String city) {
        this.address = address;
        this.state = state;
        this.city = city;
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
    public String getCity() {
        return city;
    }

}