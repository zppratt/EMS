package com.baconfiesta.ems.models.EMSUser;

import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;

/**
 * A user of the EMS system, such as a Dispatcher or System Administrator
 */
public class EMSUser implements Serializable {

    /**
     * The first name of the system user
     */
    private String firstname;

    /**
     * The last name of the system user
     */
    private String lastname;

    /**
     * The username of the system user
     */
    private String username;

    /**
     * The password of the user
     */
    private String password;

    /**
     * The records a user has created
     */
    private HashMap<Instant, EmergencyRecord> records;

    /**
     * The privilege of the user
     * True: is an administrator
     * False: is a regular user
     */
    private boolean admin;

    /**
     * The default constructor for an EMSUser
     * @param firstname the user's first name
     * @param lastname the user's last name
     * @param username the user's username
     * @param password the user's password
     * @param admin the initial privilege of the user
     */
    public EMSUser(String firstname, String lastname, String username, String password, Boolean admin) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    /**
     * Retrieve the first name of the user
     * @return the first name
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Retrieve the last name of the user
     * @return the last name
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Retrieve the username of the user
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Check a password against the user's password
     * @param password the password to check
     * @return true if the password is the same, otherwise false
     */
    public Boolean checkPassword(String password) {
        return this.getPassword().equals(password);
    }

    /**
     * Retrieve the records this user has created
     * @return the records
     */
    public HashMap<Instant, EmergencyRecord> getRecords() {
        return this.records;
    }

    /**
     * Return true if the user has administrator privilege, false otherwise
     * @return whether the user is an administrator
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Toggles the user's administrative privileges
     * @param admin true makes the user an administrator,
     *              false makes the user a regular user.
     */
    private void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Retrieves the users password
     * @return the password
     */
    private String getPassword() {
        return password;
    }

    /**
     * Prints the last, first name and username of a user
     */
    public String toString() {
        return "\"" + this.getLastname() + ", " + this.getFirstname() + "\"";
    }
}
