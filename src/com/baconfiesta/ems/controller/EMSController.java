package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;

import java.io.*;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

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
    public EMSController() throws IOException, ClassNotFoundException {
        this(null, null);
    }

    /**
     * Constructor which allows the specification of a user and database.
     * @param user the user
     * @param database the database
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSController(EMSUser user, EMSDatabase database) throws IOException, ClassNotFoundException {
        if (database==null) {
            database = new EMSDatabase();
        }
        if (user==null) {
            user = database.lookupUser("admin");
        }
        this.currentUser = user;
        this.database = database;
    }

    /**
     * Logs a user into the system
     * @param username the username
     * @param password the password
     * @return the User on success, null on failure
     */
    public EMSUser logIn(String username, String password) throws IOException, ClassNotFoundException {
        return authenticateUser(username, password);
    }

    /**
     * Logs a user out of the system
     */
    public void logOut() {

    }

    /**
     * Creates a new emergency record
     */
    public void createNewEmergency(EmergencyRecord record) {

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
    public void finalizeRecord(EmergencyRecord record) throws IOException, ClassNotFoundException {
        database.addEmergencyRecord(record);
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
    public EMSUser[] getUsers() throws IOException, ClassNotFoundException {
        return (EMSUser[]) database.getUsers().values().toArray(new EMSUser[0]);
    }

    /**
     * Retrieves a list of all the records in the system
     * @return the list of records
     */
    public EmergencyRecord[] getRecords() throws IOException, ClassNotFoundException {
        return (EmergencyRecord[]) database.getRecords().values().toArray(new EmergencyRecord[0]);
    }

    /**
     * Saves all emergency data to a file
     * @param file the file to save to
     */
    public void backupData(File file) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(database.getUsers());
        oos.writeObject(database.getRecords());
    }

    /**
     * Restores emergency data from a backup file
     * @param file the file to restore from
     */
    public void restoreData(File file) {
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))
        ) {
            database = new EMSDatabase(null, (HashMap<String, EMSUser>) ois.readObject(), (HashMap<Instant, EmergencyRecord>) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Authenticates a user
     * @param username the username
     * @param password the password
     */
    private EMSUser authenticateUser(String username, String password) throws IOException, ClassNotFoundException {
        EMSUser user = database.lookupUser(username);
        if (user != null ) {
            if (user.checkPassword(password)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Sets the current user
     * @param user the user
     */
    public void setUser(EMSUser user) {
        this.currentUser = user;
    }

    /**
     * Gets the current user
     * @return the current user of the system
     */
    public EMSUser getUser() {
        return currentUser;
    }

}
