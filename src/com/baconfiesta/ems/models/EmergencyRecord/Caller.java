package com.baconfiesta.ems.models.EmergencyRecord;

import java.io.Serializable;

/**
 * A caller in the system
 * @author team_bacon_fiesta
 */
public class Caller implements Serializable {

    /**
     * The first name of the caller
     */
    private String firstName;

    /**
     * The last name of the caller
     */
    private String lastName;

    /**
     * The phone number of the caller
     */
    private String phone;

    /**
     * Create a caller for an emergency record
     * @param firstName the first name
     * @param lastName the last name
     * @param phone the phone number
     */
    public Caller(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    /**
     * Retrieve the first name of the caller
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Retrieve the last name of the caller
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Retrieve the phone number of the caller
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set the phone number of the caller
     * @param phone the phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String toString() {
        return String.format("%s, %s", lastName, firstName);
    }
}
