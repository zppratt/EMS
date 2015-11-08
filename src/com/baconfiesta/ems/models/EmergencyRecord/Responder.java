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
    private int zip;

    /**
     * Default constructor for a responder object
     * @param phoneNumber the phone number
     * @param address the street address
     * @param state the state
     * @param zip the zip code
     */
    public Responder(String phoneNumber, String address, String state, int zip) {
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.state = state;
        this.zip = zip;
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
    public int getZip() {
        return zip;
    }

}
