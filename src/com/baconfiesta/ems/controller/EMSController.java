package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.Constants;
import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import com.baconfiesta.ems.models.EmergencyRecord.Route;
import com.baconfiesta.ems.models.EmergencyReport.EMSReport;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The main controller for the EMS system
 * @author team_bacon_fiesta
 */
public class EMSController implements Constants {

    /**
     * The current user of the system
     */
    private EMSUser currentUser;

    /**
     * The file database the system will use
     */
    static EMSDatabase _database;

    /**
     * Default constructor for a user controller
     */
    public EMSController() throws IOException, ClassNotFoundException, InterruptedException {
        this(null, null);
    }

    /**
     * Constructor which allows the specification of a user and database.
     * @param user the user
     * @param db the database
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSController(EMSUser user, EMSDatabase db) throws IOException, ClassNotFoundException, InterruptedException {

        // If database already exists, do not create a new one unless db parameter contains a different one
        if (db != null) {
            _database = db;
        } else if (_database == null) {
            _database = new EMSDatabase();
        }
        if (user != null) {
            this.currentUser = user;
        }

    }

    /**
     * Logs a user into the system
     * @param username the username
     * @param password the password
     * @return the User on success, null on failure
     */
    public EMSUser logIn(String username, char[] password) throws IOException, ClassNotFoundException {
        return authenticateUser(username, password);
    }

    /**
     * Logs a user out of the system
     */
    public void logOut() throws IOException, InterruptedException {
        _database.closeDatabase();
        _database = null;
        this.currentUser = null;
    }

    /**
     * Finds the nearest emergency responder
     * @param record the emergency record containing the address of the emergency
     */
    public void determineNearestResponder(EmergencyRecord record) {
        record.setResponder(Route.determineNearestResponder(record));
    }


    /**
     * Calculated the route from the responder to the emergency
     * @param record the emergency record containing the address of the emergency
     */
    public void calculateRoute(EmergencyRecord record) {
        record.setRoute(new Route(record.getResponder().getAddress() + ", " + record.getResponder().getCity() + "' " + record.getResponder().getState(),
                record.getLocation().getAddress() + ", " + record.getLocation().getCity() + ", " + record.getLocation().getState()));
    }

    /**
     * Saves a record to the database
     * @param record the record to finalize
     */
    public void finalizeRecord(EmergencyRecord record) throws IOException, ClassNotFoundException {
        _database.addEmergencyRecord(record);
    }

    /**
     * Retrieve an emergency record by the time it was created
     * @param time the time of the record to access
     * @return the emergency record
     */
    public EmergencyRecord accessEmergencyRecord(Instant time) throws IOException, ClassNotFoundException {
        return _database.getCachedRecords().get(time);
    }

    /**
     * Generates a report containing various statistics about emergencies
     * @param beginningDate the start date of the report
     * @param endingDate the end date of the report
     * @param exportFilename the file to save the report into
     */
    public void generateReport(Instant beginningDate, Instant endingDate, String exportFilename) throws IOException, ClassNotFoundException{
        ArrayList<EmergencyRecord> records = getRecords();
        ArrayList<EmergencyRecord> recordsInRange = new ArrayList<>();
        // Gather records in range
        records.forEach(record -> {
            if(record.getMetadata().getTimeCreated().isAfter(beginningDate) && record.getMetadata().getTimeCreated().isBefore(endingDate)) {
                recordsInRange.add(record);
            }
        });
        EmergencyRecord[] rangeRecords = new EmergencyRecord[0];
        // Turns records into an array
        rangeRecords = recordsInRange.toArray(new EmergencyRecord[recordsInRange.size()]);

        for (EmergencyRecord rangeRecord : rangeRecords) {
            System.err.println(rangeRecord);
        }

        EMSReport.generateStatsReport(rangeRecords, exportFilename);
    }

    /**
     * Retrieves a list of all the users of the system
     * @return the list of users
     */
    public ArrayList<EMSUser> getUsers() throws IOException, ClassNotFoundException {
        ArrayList<EMSUser> users = new ArrayList<>();
        _database.getCachedUsers().values().stream()
                .filter(u -> !u.isAdmin())
                .forEach(users::add);
        return users;
    }

    /**
     * Retrieves a list of all the admin users of the system
     * @return the list of users
     */
    public ArrayList<EMSUser> getAdminUsers() throws Exception {
        ArrayList<EMSUser> users = new ArrayList<>();
        _database.getCachedUsers().values().stream()
                .filter(EMSUser::isAdmin)
                .forEach(users::add);
        return users;
    }

    /**
     * Retrieves a list of all the records in the system
     * @return the list of records
     */
    public ArrayList<EmergencyRecord> getRecords() throws IOException, ClassNotFoundException {
        ArrayList<EmergencyRecord> list = new ArrayList<>();

        _database.getCachedRecords().entrySet().stream()
                .sorted(Map.Entry.<Instant, EmergencyRecord>comparingByKey().reversed())
                .forEach(r -> list.add(r.getValue()));

        return list;
    }

    /**
     * Retrieves a sorted list of the last twenty records
     * @return the descending list of records
     * @throws IOException can be caught from getCachedRecords
     * @throws ClassNotFoundException can be caught from getCachedRecords
     */
    public EmergencyRecord[] getRecentRecords() throws IOException, ClassNotFoundException {

        ArrayList<EmergencyRecord> list = new ArrayList<>();

        _database.getCachedRecords().entrySet().stream()
                .sorted(Map.Entry.<Instant, EmergencyRecord>comparingByKey().reversed())
                .limit(20)
                .forEach(r -> list.add(r.getValue()));

        return list.toArray(new EmergencyRecord[20]);
    }

    /**
     * Saves all emergency data to a file
     * @param file the file to save to
     */
    public void backupData(File file) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(_database.getCachedUsers());
        oos.writeObject(_database.getCachedRecords());
    }

    /**
     * Restores emergency data from a backup file
     * @param file the file to restore from
     */
    public void restoreData(File file) throws InterruptedException, IOException, ClassNotFoundException {
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))
        ) {
            _database = new EMSDatabase(null, (HashMap<String, EMSUser>) ois.readObject(), (HashMap<Instant, EmergencyRecord>) ois.readObject());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Authenticates a user
     * @param username the username
     * @param password the password
     */
    protected static EMSUser authenticateUser(String username, char[] password) throws
            NullPointerException, IOException, ClassNotFoundException {
        EMSUser user = _database.lookupUser(username);
        if (user != null ) {
            user = user.checkPassword(password) ? user : null;
        }
        return user;
    }

    /**
     * Sets the current user
     * @param user the user
     */
    void setUser(EMSUser user) {
        this.currentUser = user;
    }

    /**
     * Gets the current user
     * @return the current user of the system
     */
    public EMSUser getCurrentUser() {
        return currentUser;
    }

}
