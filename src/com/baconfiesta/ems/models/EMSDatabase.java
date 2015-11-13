package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EMSUser.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;

import java.io.*;
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
     * Path for the database
     */
    private static final Path databasePath = Paths.get("db/database.db");

    /**
     * The file for the user database
     */
    private static final File database = databasePath.toFile();

    /**
     * Output stream for storing the users and records
     */
    private ObjectOutputStream outputStream;

    /**
     * Input stream for receiving the users and records
     */
    private ObjectInputStream inputStream;

    /**
     * File output stream for the database
     */
    private FileOutputStream fileOutputStream;

    /**
     * File input stream for the database
     */
    private FileInputStream fileInputStream;

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
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSDatabase() throws IOException, ClassNotFoundException {
        this(database);
    }

    /**
     * Constructor for a database object specifying a database location
     * @param file the database file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSDatabase(File file) throws IOException, ClassNotFoundException {
        this(file, null, null);
    }

    /**
     * Create a database, specifying the file, users, and records (for restoration of data)
     * @param file if null, default path is used for the database
     * @param users the users
     * @param records the records
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EMSDatabase(File file, HashMap<String, EMSUser> users, HashMap<Instant, EmergencyRecord> records)
            throws IOException, ClassNotFoundException {
        this.users = users;
        this.records = records;
        // Check if the streams for the database reading and writing have been created. If not, create them.
        setupStreams(file);
        // Check if the user and records are initialized in memory
        setupUserDatabase();
        setupRecordDatabase();
        isOpen = true;
    }

    /**
     * Check input and output streams for setup
     * @param file the file to check as a database
     * @throws IOException
     */
    private void setupStreams(File file) throws IOException {
        if (file==null) {
            file = database;
        }
        closeStreams();
        File directory = new File("db");
        if (!(directory.isDirectory())) {
            directory.mkdir();
        }
        fileOutputStream = new FileOutputStream(file);
        outputStream = new ObjectOutputStream(fileOutputStream);
        fileInputStream = new FileInputStream(database);
        inputStream = new ObjectInputStream(fileInputStream);
    }

    /**
     * Closes all streams
     * @throws IOException
     */
    private void closeStreams() throws IOException {
        if (fileInputStream!=null) {
            fileInputStream.close();
        }
        if (fileOutputStream!=null) {
            fileOutputStream.close();
        }
        if (inputStream!=null) {
            inputStream.close();
        }
        if (outputStream!=null) {
            outputStream.close();
        }
    }

    /**
     * Setup the memory space for the user
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void setupUserDatabase() throws IOException, ClassNotFoundException {
        if (users == null) {
            users = getDatabaseUsers();
            if (users==null) {
                users = new HashMap<>();
                // Default user
                EMSUser user = new EMSUser("Adminy", "Administrator", "admin", "admin", true);
                users.put(user.getUsername(), user);
            }
        } else {
            reconcileDatabaseWithMemory(); // if there are users in memory already, compare to database
        }
    }

    /**
     * Setup the memory space for the records
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void setupRecordDatabase() throws IOException, ClassNotFoundException {
        if (records == null) {
            records = getDatabaseRecords();
            if (records==null) {
                records = new HashMap<>();
            }
        } else {
            reconcileDatabaseWithMemory(); // if there are records in memory already, compare to database
        }
    }

    /**
     * Adds any new users or records to database from memory and vice versa, simply performs a union
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
     * @param object the object to write out
     * @throws IOException
     */
    private void writeObject(Object object) throws IOException {
        outputStream.writeObject(object);
        System.out.println("Writing: " + object + " to the database file.");
        outputStream.flush();
        fileOutputStream.flush();
    }

    /**
     * Retrieves an emergency record from the database
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
     * @param firstname the first name
     * @param lastname the last name
     * @param username the username
     * @param password the password
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
     * @throws IOException
     */
    public void closeDatabase() throws IOException {
        closeStreams();
        users = null;
        records = null;
        isOpen = false;
    }

    /**
     * Retrieve the list of emergency records in the database
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
     * @return the records on success, null on failure
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Map<Instant, EmergencyRecord> getDatabaseRecords() throws IOException, ClassNotFoundException {
//        System.out.println("Try to get records from database...");
        try {
            @SuppressWarnings("unchecked")
            HashMap<Instant, EmergencyRecord> records = (HashMap<Instant, EmergencyRecord>) inputStream.readObject();
            for (Instant  k : records.keySet());
            for (EmergencyRecord v : records.values());
        } catch (IOException e) {
//            System.out.println("Database file is empty.");
        }
        return records;
    }

    /**
     * Attempt to retrieve users from database
     * @return the users on success, null on failure
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Map<String, EMSUser> getDatabaseUsers() throws IOException, ClassNotFoundException {
//        System.out.println("Try to get users from database...");
        try {
            @SuppressWarnings("unchecked")
            HashMap<String, EMSUser> users = (HashMap<String, EMSUser>) inputStream.readObject();
            for (String k : users.keySet());
            for (EMSUser v : users.values());
        } catch (IOException e) {
//            System.out.println("Database file is empty.");
        }
        return users;
    }

    /**
     * Returns whether the database is open
     * @return true if open, false if closed
     */
    boolean isOpen() {
        return isOpen;
    }
}
