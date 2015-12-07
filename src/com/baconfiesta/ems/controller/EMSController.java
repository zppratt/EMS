package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.Constants;
import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import com.baconfiesta.ems.models.EmergencyRecord.Responder;
import com.baconfiesta.ems.models.EmergencyRecord.Route;
import com.baconfiesta.ems.models.EmergencyReport.EMSReport;
import javassist.tools.rmi.ObjectNotFoundException;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    static EMSDatabase database;

    /**
     * Default constructor for a user controller
     */
    public EMSController() throws IOException, ClassNotFoundException, InterruptedException {
        this(null, null);
    }

    /**
     * Constructor which allows the specification of a user and database.
     * @param user the user
     * @param database the database
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSController(EMSUser user, EMSDatabase database) throws IOException, ClassNotFoundException, InterruptedException {
        // If database already exists, do not create a new one unless db parameter contains a different one
        if (database != null) {
            EMSController.database = database;
        } else if (EMSController.database == null) {
            EMSController.database = new EMSDatabase();
        }
        if (user != null) {
            setUser(user);
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
        database.closeDatabase();
        database = null;
        this.currentUser = null;
    }

    /**
     * Finds the nearest emergency responder
     * @param mainRecord the main record that will contain the main route
     * @param alternativeRecord the alternative record that will contain the alternate route
     */
    public void determineNearestResponders(EmergencyRecord mainRecord, EmergencyRecord alternativeRecord)
            throws ObjectNotFoundException, ArrayIndexOutOfBoundsException, IOException {
        Responder responders[] = Route.determineNearestResponders(mainRecord);
        mainRecord.setResponder(responders[0]);
        alternativeRecord.setResponder(responders[1]);
    }


    /**
     * Calculated the route from the responder to the emergency
     * @param record the emergency record containing the address of the emergency
     */
    public void calculateRoute(EmergencyRecord record, Boolean alternateRoute)
            throws ObjectNotFoundException, ArrayIndexOutOfBoundsException, IOException {
        record.setRoute(new Route(record.getResponder().getAddress() + ", " + record.getResponder().getCity() + ", " + record.getResponder().getState(),
                record.getLocation().getAddress() + ", " + record.getLocation().getCity() + ", " + record.getLocation().getState(), alternateRoute));
    }

    /**
     * Saves a record to the database
     * @param record the record to finalize
     */
    public void finalizeRecord(EmergencyRecord record) throws IOException, ClassNotFoundException {
        currentUser.addRecord(record);
        database.addEmergencyRecord(record);
    }

    /**
     * Retrieve an emergency record by the time it was created
     * @param time the time of the record to access
     * @return the emergency record
     */
    public EmergencyRecord accessEmergencyRecord(Instant time) throws IOException, ClassNotFoundException {
        return database.getCachedRecords().get(time);
    }



    /**
     * Generates a report containing various statistics about emergencies
     * @param beginningDate the start date of the report
     * @param endingDate the end date of the report
     * @param filename the file to save the report into
     */
    public void generateReport(Instant beginningDate, Instant endingDate, String filename) throws IOException, ClassNotFoundException{
        ArrayList<EmergencyRecord> records = getRecords();
        ArrayList<EmergencyRecord> recordsInRange = new ArrayList<>();
        // Gather records in range
        records.forEach(record -> {
            if(record.getMetadata().getTimeCreated().isAfter(beginningDate) && record.getMetadata().getTimeCreated().isBefore(endingDate)) {
                recordsInRange.add(record);
            }
        });
        EmergencyRecord[] rangeRecords;
        // Turns records into an array
        rangeRecords = recordsInRange.toArray(new EmergencyRecord[recordsInRange.size()]);
        EMSReport.generateStatsReport(rangeRecords, filename);
    }

    /**
     * Generates a report for a single record
     * @param record the record to generate a report for
     * @param fileName the filename to save the report to
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void generateReport(EmergencyRecord record, String fileName) throws IOException, ClassNotFoundException {
        EMSReport.generateRecordReport(record, fileName);
    }

    /**
     * Retrieves a list of all the users of the system
     * @return the list of users
     */
    public ArrayList<EMSUser> getUsers() throws IOException, ClassNotFoundException {
        ArrayList<EMSUser> users = new ArrayList<>();
        database.getCachedUsers().values().stream()
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
        database.getCachedUsers().values().stream()
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

        database.getCachedRecords().entrySet().stream()
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

        database.getCachedRecords().entrySet().stream()
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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            Serializable[] toWrite = {
                    (Serializable) database.getCachedUsers(),
                    (Serializable) database.getCachedRecords()
            };
            oos.writeObject(toWrite);
        }
    }

    /**
     * Restores emergency data from a backup file
     * @param file the file to restore from
     */
    @SuppressWarnings("unchecked")
    public void restoreData(File file) throws InterruptedException, IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Serializable[] readObjects = (Serializable[]) ois.readObject();
            HashMap<String, EMSUser> users = (HashMap<String, EMSUser>) readObjects[0];
            HashMap<Instant, EmergencyRecord> records = (HashMap<Instant, EmergencyRecord>) readObjects[1];
            database.closeDatabase();
            database = new EMSDatabase(null, users, records);
        }
    }

    /**
     * Authenticates a user
     * @param username the username
     * @param password the password
     */
    protected static EMSUser authenticateUser(String username, char[] password) throws
            NullPointerException, IOException, ClassNotFoundException {
        EMSUser user = database.lookupUser(username);
        if (user != null ) {
            user = user.checkPassword(password) ? user : null;
        }
        return user;
    }

    /**
     * Sets the current user
     * @param user the user
     */
    EMSUser setUser(EMSUser user) {
        currentUser = user;
        return currentUser;
    }

    /**
     * Gets the current user
     * @return the current user of the system
     */
    public EMSUser getCurrentUser() {
        return currentUser;
    }

}
