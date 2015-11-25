package com.baconfiesta.ems.models.EmergencyRecord;

import java.io.Serializable;

/**
 * A responder to an emergency
 * @author team_bacon_fiesta
 */
public class Responder implements Serializable {

    /**
     * The phone number of the responder
     */
    private String phoneNumber;

    /**
     * The street address where the responder is located
     */
    private String address;

    /**
     * The state where the responder is located
     */
    private String state;

    /**
     * The zip code where the responder is located
     */
    private String city;

    /**
     * Default constructor for a responder object
     * @param phoneNumber the phone number
     * @param address the street address
     * @param state the state
     * @param city the zip code
     */
    public Responder(String phoneNumber, String address, String state, String city) {
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.state = state;
        this.city = city;
    }

    /**
     * Retrieve the phone number of the responder
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Retrieve the street address of the responder
     * @return the street address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Retrieve the state where the responder is/was located
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Retrieve the zip code where the responder is/was located
     * @return the zip code
     */
    public String getZip() {
        return city;
    }

}
