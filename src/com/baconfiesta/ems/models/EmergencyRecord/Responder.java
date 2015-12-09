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
    private final String phoneNumber;

    /**
     * The street address where the responder is located
     */
    private final String address;

    /**
     * The state where the responder is located
     */
    private final String state;

    /**
     * The city where the responder is located
     */
    private final String city;

    /**
     * Default constructor for a responder object
     * @param phoneNumber the phone number
     * @param address the street address
     * @param state the state
     * @param city the city
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
     * Retrieve the city where the responder is/was located
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Returns the responder in paragraph form
     * @return the responder in paragraph form
     */
    public String getParagraphForm(){
        String paragraph = "";

        paragraph += "\nResponder Information:\n";
        paragraph += "Phone: " + getPhoneNumber() + "\n";
        paragraph += "Address: " + getAddress() + "\n";
        paragraph += "State: " + getState() + "\n";
        paragraph += "City: " + getCity() + "\n";

        return paragraph;
    }

}
