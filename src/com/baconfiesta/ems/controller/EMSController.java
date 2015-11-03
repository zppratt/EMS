package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;

import java.io.File;
import java.time.Instant;
import java.util.Date;

/**
 * The main controller for the EMS system
 * @author team_bacon_fiesta
 */
public class EMSController {

    /**
     * The current user of the system
     */
    private EMSUser currentUser;

    /**
     * The file database the system will use
     */
    private EMSDatabase database;

    /**
     * Default constructor for a user controller
     */
    public EMSController() {
    }

    /**
     * Logs a user into the system
     * @param username the username
     * @param password the password
     * @return the User on success, null on failure
     */
    public EMSUser logIn(String username, String password) {
        return null;
    }

    /**
     * Logs a user out of the system
     */
    public void logOut() {

    }

    /**
     * Creates a new emergency record
     * @param a unknown
     * @param b unknown
     * @param c unknown
     * @param d unknown
     * @param e unknown
     * @param f unknown
     * @param g unknown
     * @param h unknown
     * @return the created record on success, null on failure
     */
    public EmergencyRecord createNewEmergency(
            String a, String b, String c,
            String d, String e, String f,
            int g, String h) {
        return null;
    }

    /**
     * Finds the nearest emergency responder
     * @param record the emergency record containing the address of the emergency
     */
    private void determineNearestResponder(EmergencyRecord record) {

    }

    /**
     * Calculated the route from the responder to the emergency
     * @param record the emergency record containing the address of the emergency
     */
    public void calculateRoute(EmergencyRecord record) {

    }

    /**
     * Saves a record to the database
     * @param record the record to finalize
     */
    public void finalizeRecord(EmergencyRecord record) {

    }

    /**
     * Retrieve an emergency record by the time it was created
     * @param time the time of the record to access
     * @return the emergency record
     */
    public EmergencyRecord accessEmergencyRecord(Instant time) {
        return null;
    }

    /**
     * Generates a report containing various statistics about emergencies
     * @param beginningDate the start date of the report
     * @param endingDate the end date of the report
     * @param exportFile the file to save the report into
     */
    public void generateReport(Date beginningDate, Date endingDate, File exportFile) {

    }

    /**
     * Retrieves a list of all the users of the system
     * @return the list of users
     */
    public EMSUser[] getUsers() {
        return null;
    }

    /**
     * Retrieves a list of all the records in the system
     * @return the list of records
     */
    public EmergencyRecord[] getRecords() {
        return null;
    }

    /**
     * Saves all emergency data to a file
     * @param backupFile the file to save to
     */
    public void backupData(File backupFile) {

    }

    /**
     * Restores emergency data from a backup file
     * @param restoringFile the file to restore from
     */
    public void restoreData(File restoringFile) {

    }

}
