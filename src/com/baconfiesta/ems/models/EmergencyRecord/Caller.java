package com.baconfiesta.ems.models.EmergencyRecord;

/**
 * A caller in the system
 */
public class Caller {

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
     * @param firstName
     * @param lastName
     * @param phone
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
     * Set the first name of the caller
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Retrieve the last name of the caller
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the last name of the caller
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
}
