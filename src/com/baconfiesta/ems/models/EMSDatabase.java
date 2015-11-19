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
     * Default path for the database
     */
    private static final Path databasePath = Paths.get("./db/database.db");

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
    private boolean isOpen;

    /**
     * Default constructor for a database object
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSDatabase() throws IOException, ClassNotFoundException {
        this(null);
    }

    /**
     * Constructor for a database object specifying a database location
     *
     * @param file the database file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSDatabase(File file) throws IOException, ClassNotFoundException {
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
            throws IOException, ClassNotFoundException {
        System.out.println("Database constructor. File : " + file);
        this.users = users;
        this.records = records;
        setupDatabase(file);
    }

    /**
     * Setup database file (check for existence, etc.)
     */
    private void setupDatabase(File file) throws IOException, ClassNotFoundException {
        // If no file is specified, use default path. Otherwise, set this object's file to the specified file.
        if (file != null) {
            database = file;
        } else {
            database = databasePath.toFile();
        }
        if (Files.notExists(database.toPath())) {
            Files.createFile(database.toPath());
        }
        setupUserDatabase();
        setupRecordDatabase();
        isOpen = true;
    }

    /**
     * Setup the memory space for the user
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void setupUserDatabase() throws IOException, ClassNotFoundException {
        if (users == null) {
            users = getDatabaseUsers();
            if (users == null) { // if still null
                System.out.println("Creating new users object...");
                users = new HashMap<>();
                // Default user
                EMSUser user = new EMSUser("Adminy", "Administrator", "", "", true);
                users.put(user.getUsername(), user);
            }
        } else {
            reconcileDatabaseWithMemory(); // if there are users in memory already, compare to database
        }
    }

    /**
     * Setup the memory space for the records
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void setupRecordDatabase() throws IOException, ClassNotFoundException {
        if (records == null) {
            records = getDatabaseRecords();
            if (records == null) {
                records = new HashMap<>();
            }
        } else {
            reconcileDatabaseWithMemory(); // if there are records in memory already, compare to database
        }
    }

    /**
     * Adds any new users or records to database from memory and vice versa, simply performs a union
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void reconcileDatabaseWithMemory() throws IOException, ClassNotFoundException {
        // Get users and records from database
        Map<String, EMSUser> databaseUsers = getDatabaseUsers();
        Map<Instant, EmergencyRecord> databaseRecords = getDatabaseRecords();
        if (databaseUsers != null) {
            users.putAll(databaseUsers);
        }
        if (databaseRecords != null) {
            records.putAll(databaseRecords);
        }
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
    public EMSUser verifyUser(String username, String password) throws IOException, ClassNotFoundException {
        // If the user is in the database, check the password
        if (this.getUsers().containsKey(username)) {
            EMSUser userToCheck = this.getUsers().get(username);
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
        if (!this.getRecords().containsValue(record)) {
            this.getRecords().put(record.getMetadata().getTimeCreated(), record);
            writeObject(this.getRecords());
        }
    }

    /**
     * Writes an object out to the database file
     *
     * @param object the object to write out
     * @throws IOException
     */
    private void writeObject(Object object) throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(database, true);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(object);
            System.out.println("Writing: " + object + " to the database file.");
            oos.flush();
            fos.flush();
        }
    }

    /**
     * Retrieves an emergency record from the database
     *
     * @param time the time the record was created
     * @return the record
     */
    public EmergencyRecord getEmergencyRecord(Instant time) throws IOException, ClassNotFoundException {
        if (this.getRecords().containsKey(time)) {
            return this.getRecords().get(time);
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
        this.getUsers().put(username, user);
        users.put(user.getUsername(), user);
        writeObject(this.getUsers());
        reconcileDatabaseWithMemory();
        return user;
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
        if (this.getUsers().containsKey(username)) {
            return this.getUsers().get(username);
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
        if (this.getRecords().containsKey(time)) {
            return this.getRecords().get(time);
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
        if (this.getUsers().containsKey(username)) {
            this.getUsers().remove(username);
            writeObject(users);
            reconcileDatabaseWithMemory();
            return true;
        }
        return false;
    }

    /**
     * Closes the streams to the database and any other cleanup
     *
     * @throws IOException
     */
    public void closeDatabase() throws IOException {
        users = null;
        records = null;
        isOpen = false;
    }

    /**
     * Retrieve the list of emergency records in the database
     *
     * @return the list of records
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Map<Instant, EmergencyRecord> getRecords() throws IOException, ClassNotFoundException {
        reconcileDatabaseWithMemory();
        return records;
    }

    /**
     * Retrieve the list of users in the database
     *
     * @return the list of user
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Map<String, EMSUser> getUsers() throws IOException, ClassNotFoundException {
        reconcileDatabaseWithMemory();
        return users;
    }

    /**
     * Attempt to retrieve records from database
     *
     * @return the records on success, null on failure
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Map<Instant, EmergencyRecord> getDatabaseRecords() throws IOException, ClassNotFoundException {
//        System.out.println("Try to get records from database...");
        Map<Instant, EmergencyRecord> tempRecords;
        if (!Files.exists(databasePath)) {
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(database);
                ObjectInputStream is = new ObjectInputStream(fis)
        ) {
            tempRecords = (HashMap<Instant, EmergencyRecord>) is.readObject();
            // These lines check each element for
            // validity by accessing them
            for (Instant k : records.keySet()) ;
            for (EmergencyRecord v : records.values()) ;
        } catch (Exception e) {
            tempRecords = records;
        }
        return tempRecords;
    }

    /**
     * Attempt to retrieve users from database
     *
     * @return the users on success, null on failure
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Map<String, EMSUser> getDatabaseUsers() throws IOException, ClassNotFoundException {
//        System.out.println("Try to get users from database...");
        Map<String, EMSUser> tempUsers;
        if (!Files.exists(databasePath)) {
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(database);
                ObjectInputStream is = new ObjectInputStream(fis)
        ) {
            tempUsers = (HashMap<String, EMSUser>) is.readObject();
            for (String k : users.keySet()) ;
            for (EMSUser v : users.values()) ;
        } catch (Exception e) {
            tempUsers = users;
        }
        return tempUsers;
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
