package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;

import java.time.Instant;
import java.util.HashMap;

/**
 * The file storage for records and users
 */
public class EMSDatabase {

    /**
     * List of users
     * key: username, value: the user
     */
    private HashMap<String, EMSUser> users;

    /**
     * List of emergency records
     * key: the creation time
     * value: the emergency record
     */
    private HashMap<Instant, EmergencyRecord> records;

    /**
     * Checks a users credentials match in the database
     * @param username the user id
     * @param password the password
     * @return the user on succcess, null on failure
     */
    public EMSUser verifyUser(String username, String password) {
        // If the user is in the database, check the password
        if (this.getUsers().containsKey(username)) {
            EMSUser userToCheck = this.getUsers().get(username);
            return userToCheck.checkPassword(password) ? userToCheck : null;
        }
        return null;
    }

    /**
     * Adds an emergency record to the database
     * @param record the record to add
     */
    public void addEmergencyRecord(EmergencyRecord record) {
        if (!this.getRecords().containsValue(record)) {
            this.getRecords().put(record.getMetadata().getTimeCreated(), record);
        }
    }

    /**
     * Retrieves an emergency record from the database
     * @param time the time the record was created
     * @return the record
     */
    public EmergencyRecord getEmergencyRecord(Instant time) {
        if (this.getRecords().containsKey(time)) {
            return this.getRecords().get(time);
        }
        return null;
    }

    /**
     * Create a user and add them to the database
     * @param firstname the first name
     * @param lastname the last name
     * @param username the username
     * @param password the password
     * @return the user on success, null on failure
     */
    public EMSUser addUser(String firstname, String lastname, String username, String password) {
        // Create a user object
        EMSUser user = new EMSUser(firstname, lastname, username, password, false);
        // Add it to the database
        this.getUsers().put(username, user);
        // ...
        return null;
    }

    /**
     * Lookup a user by username
     * @param username the username
     * @return the user on success, null on failure
     */
    public EMSUser lookupUser(String username) {
        // Return the user if it exists
        if (this.getUsers().containsKey(username)) {
            return this.getUsers().get(username);
        }
        return null;
    }

    /**
     * Retrieve an emergency record by the time it was created
     * @param time the creation time
     * @return the emergency record on success, null on failure
     */
    public EmergencyRecord lookupEmergencyRecord(Instant time) {
        // Return the record for this time if it exists
        if (this.getRecords().containsKey(time)) {
            return this.getRecords().get(time);
        }
        return null;
    }

    /**
     * Remove a user from the database by username
     * @param username the username of the user to remove
     * @return true on success, false on failure
     */
    public boolean removeUser(String username) {
        // Remove a user from the database and list in memory
        if (this.getUsers().containsKey(username)) {
            this.getUsers().remove(username);
            return true;
        }
        return false;
    }

    /**
     * Retrieve the list of emergency records in the database
     * @return the list of records
     */
    private HashMap<Instant, EmergencyRecord> getRecords() {
        return records;
    }

    /**
     * Retrieve the list of users in the database
     * @return the list of users
     */
    private HashMap<String, EMSUser> getUsers() {
        return users;
    }
}
