package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EMSUser.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * The file storage for records and users
 */
public class EMSDatabase {

    /**
     * Default path for the database dir
     */
    private static final String databaseDirPath = "./db";

    /**
     * Default path for the database
     */
    private static final Path databasePath = Paths.get(databaseDirPath + "/database.db");

    /**
     * The file for the user database
     */
    private File database;

    /**
     * List of users
     * key: username, value: the user
     */
    private Map<String, EMSUser> users;

    /**
     * List of emergency records
     * key: the creation time
     * value: the emergency record
     */
    private Map<Instant, EmergencyRecord> records;

    /**
     * Whether the database is open or not
     */
    private static boolean isOpen;

    /**
     * Default constructor for a database object
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSDatabase() throws IOException, ClassNotFoundException, InterruptedException {
        this(null);
    }

    /**
     * Constructor for a database object specifying a database location
     *
     * @param file the database file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSDatabase(File file) throws IOException, ClassNotFoundException, InterruptedException {
        this(file, null, null);
    }

    /**
     * Create a database, specifying the file, users, and records (for restoration of data)
     *
     * @param file    if null, default path is used for the database
     * @param users   the users
     * @param records the records
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSDatabase(File file, HashMap<String, EMSUser> users, HashMap<Instant, EmergencyRecord> records)
            throws IOException, ClassNotFoundException, InterruptedException {
        if (isOpen()) {
            return;
        }
        setupDatabase(file);
        setupCache(users, records);
    }

    /**
     * Setup memory space for users and records
     */
    private void setupCache(HashMap<String, EMSUser> users, HashMap<Instant, EmergencyRecord> records) throws IOException, ClassNotFoundException {
        if (users == null) {
            this.users = getDatabaseUsers();
            if (this.users == null) {
                this.users = new HashMap<>();
                this.users.put("", new EMSUser("Default","","","", true));
            }
        } else {
            this.users = users;
        }
        if (records == null) {
            this.records = getDatabaseRecords();
            if (this.records == null) {
                this.records = new HashMap<>();
            }
        } else {
            this.records = records;
        }
    }

    /**
     * Setup database file (check for existence, etc.)
     */
    private void setupDatabase(File file) throws IOException, ClassNotFoundException {
        if (!Files.isDirectory(Paths.get(databaseDirPath))) {
            Files.createDirectory(Paths.get(databaseDirPath));
        }
        // If no file is specified, use default path. Otherwise, set this object's file to the specified file.
        if (file != null) {
            database = file;
        } else {
            database = databasePath.toFile();
        }
        if (Files.notExists(database.toPath())) {
            Files.createFile(database.toPath());
        }
        isOpen = true;
    }

    /**
     * Closes the the database and any other cleanup
     *
     * @throws IOException
     */
    public void closeDatabase() throws IOException, InterruptedException {
        users = null;
        records = null;
        isOpen = false;
    }

    /**
     * Checks a user's credentials match in the database
     *
     * @param username the user id
     * @param password the password
     * @return the user on succcess, null on failure
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSUser verifyUser(String username, char[] password) throws IOException, ClassNotFoundException {
        // If the user is in the database, check the password
        if (users.containsKey(username)) {
            EMSUser userToCheck = users.get(username);
            return userToCheck.checkPassword(password) ? userToCheck : null;
        }
        return null;
    }

    /**
     * Adds an emergency record to the database
     *
     * @param record the record to add
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void addEmergencyRecord(EmergencyRecord record) throws IOException, ClassNotFoundException {
        records.put(record.getMetadata().getTimeCreated(), record);
        writeCacheToDatabase();
    }

    public boolean removeRecord(EmergencyRecord record) throws IOException, NullPointerException {
        records.remove(record.getMetadata().getTimeCreated());
        writeCacheToDatabase();
        return true;
    }

    /**
     * Writes an object out to the database file
     *
     * @throws IOException
     */
    private void writeCacheToDatabase() throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(database, false);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            Serializable[] array = new Serializable[2];
            array[0] = (Serializable) users;
            array[1] = (Serializable) records;
            oos.writeObject(array);
            oos.flush();
        }
    }

    /**
     * Retrieves an emergency record from the database
     *
     * @param time the time the record was created
     * @return the record
     */
    public EmergencyRecord getEmergencyRecord(Instant time) throws IOException, ClassNotFoundException {
        if (this.getCachedRecords().containsKey(time)) {
            return this.getCachedRecords().get(time);
        }
        return null;
    }

    /**
     * Create a user and add them to the database
     *
     * @param firstname the first name
     * @param lastname  the last name
     * @param username  the username
     * @param password  the password
     * @return the user on success, null on failure
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSUser addUser(String firstname, String lastname, String username, String password) throws IOException, ClassNotFoundException {
        // Create a user object
        EMSUser user = new EMSUser(firstname, lastname, username, password, false);
        // Add it to the database
        users.put(user.getUsername(), user);
        writeCacheToDatabase();
        return user;
    }

//    /**
//     * Add a user to the database.
//     * @param user the user to add
//     * @param password the user's password
//     * @return the user
//     * @throws IOException
//     * @throws ClassNotFoundException
//     */
//    public EMSUser addUser(EMSUser user, String password) throws IOException, ClassNotFoundException {
//        return addUser(
//                user.getFirstname(),
//                user.getLastname(),
//                user.getUsername(),
//                password
//        );
//    }

    /**
     * Makes user admin
     */
    public void setUserAdmin(String username, boolean admin) throws IOException {
        users.get(username).setAdmin(admin);
        writeCacheToDatabase();
    }

    /**
     * Lookup a user by username
     *
     * @param username the username
     * @return the user on success, null on failure
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSUser lookupUser(String username) throws IOException, ClassNotFoundException {
        // Return the user if it exists
        if (users.containsKey(username)) {
            return users.get(username);
        }
        return null;
    }

    /**
     * Retrieve an emergency record by the time it was created
     *
     * @param time the creation time
     * @return the emergency record on success, null on failure
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EmergencyRecord lookupEmergencyRecord(Instant time) throws IOException, ClassNotFoundException {
        // Return the record for this time if it exists
        if (this.getCachedRecords().containsKey(time)) {
            return this.getCachedRecords().get(time);
        }
        return null;
    }

    /**
     * Remove a user from the database by username
     *
     * @param username the username of the user to remove
     * @return true on success, false on failure
     */
    public boolean removeUser(String username) throws IOException, ClassNotFoundException {
        // Remove a user from the list
        if (this.getCachedUsers().containsKey(username)) {
            this.getCachedUsers().remove(username);
            writeCacheToDatabase();
            return true;
        }
        return false;
    }

    /**
     * Retrieve the list of emergency records in the database
     *
     * @return the list of records
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Map<Instant, EmergencyRecord> getCachedRecords() throws IOException, ClassNotFoundException {
        return records;
    }

    /**
     * Retrieve the list of users in the database
     *
     * @return the list of user
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Map<String, EMSUser> getCachedUsers() throws IOException, ClassNotFoundException {
        return users;
    }

    /**
     * Attempt to retrieve records from database
     *
     * @return the records on success, null on failure
     */
    @SuppressWarnings({"unchecked", "unused", "StatementWithEmptyBody"})
    synchronized Map<Instant, EmergencyRecord> getDatabaseRecords() throws IOException, ClassNotFoundException {
        try (
                FileInputStream fis = new FileInputStream(database);
                ObjectInputStream is = new ObjectInputStream(fis)
        ) {
            Map<Instant, EmergencyRecord> records =
                    (HashMap<Instant, EmergencyRecord>) ((Serializable[]) is.readObject())[1];
            // These lines check each element for validity by accessing them
            for (Instant k : records.keySet()) ;
            for (EmergencyRecord v : records.values()) ;
            return records;
        } catch (Exception e) {
            return this.records;
        }
    }

    /**
     * Attempt to retrieve users from database
     *
     * @return the users on success, null on failure
     */
    @SuppressWarnings({"unchecked", "unused", "StatementWithEmptyBody"})
    synchronized Map<String, EMSUser> getDatabaseUsers() throws IOException, ClassNotFoundException {
        try (
                FileInputStream fis = new FileInputStream(database);
                ObjectInputStream is = new ObjectInputStream(fis)
        ) {
            Map<String, EMSUser> users = (Map<String, EMSUser>)((Serializable[]) is.readObject())[0];
            // These lines check each element for validity by accessing them
            for (String k : users.keySet()) ;
            for (EMSUser v : users.values()) ;
            return users;
        } catch (Exception e) {
            return this.users;
        }
    }

    /**
     * Returns whether the database is open
     *
     * @return true if open, false if closed
     */
    boolean isOpen() {
        return isOpen;
    }
}
