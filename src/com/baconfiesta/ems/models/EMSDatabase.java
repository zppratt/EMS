package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * The file storage for records and users
 */
public class EMSDatabase {

    /**
     * The file for the user database
     */
    private static final File database = new File("./db/database.db");

    /**
     * Log for debugging, etc.
     */
    private Logger log = Logger.getLogger(EMSDatabase.class.getName());

    /**
     * Output stream for storing the users and records
     */
    private static ObjectOutputStream outputStream;

    /**
     * Input stream for receiving the users and records
     */
    private static ObjectInputStream inputStream;

    /**
     * File output stream for the database
     */
    private static FileOutputStream fileOutputStream;

    /**
     * File input stream for the database
     */
    private static FileInputStream fileInputStream;

    /**
     * List of users
     * key: username, value: the user
     */
    private static Map<String, EMSUser> users;

    /**
     * List of emergency records
     * key: the creation time
     * value: the emergency record
     */
    private static Map<Instant, EmergencyRecord> records;

    public EMSDatabase() {
    }

    /**
     * Default builder for a database object
     */
    public static EMSDatabase getNewDatabase() {
        return new EMSDatabase().withFile(database);
    }

    /**
     * Builder for a database object specifying a database location
     */
    public EMSDatabase withFile(File file) {
        // Check if the streams for the database reading and writing have been created. If not, create them.
        setupStreams(file);
        // Setup logger
        setupLogger();
        // Check if the user and records are initialized in memory
        setupUserDatabase();
        setupRecordDatabase();
        return new EMSDatabase();
    }

    /**
     * Builder that allows the specification of a user
     */
    public EMSDatabase withUsers(Map<String, EMSUser> users)  {
        EMSDatabase.users = users;
        return new EMSDatabase();
    }

    /**
     * Builder that allows the specification of a user
     */
    public EMSDatabase withRecords( Map<Instant, EmergencyRecord> records)  {
        EMSDatabase.records = records;
        return new EMSDatabase();
    }

    /**
     * Check input and output streams for setup
     */
    private void setupStreams(File file) {
        if (outputStream==null) {
            if (fileOutputStream==null) {
                try {
                    File directory = new File("./db");
                    if (!(directory.isDirectory())) {
                        directory.mkdir();
                    }
                    fileOutputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                outputStream = new ObjectOutputStream(fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream==null) {
            if (fileInputStream==null) {
                try {
                    fileInputStream = new FileInputStream(database);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                inputStream = new ObjectInputStream(fileInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets up the logger
     */
    private void setupLogger() {
        Logger logger = Logger.getLogger(this.getClass().toString());
        String log4JPropertyFile = "./log/log4j.properties";
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(log4JPropertyFile));
            PropertyConfigurator.configure(p);
            logger.info("New log created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setup the memory space for the user
     */
    private void setupUserDatabase() {
        if (users == null) {
            users = getDatabaseUsers();
            if (users==null) {
                users = new HashMap<>();
            }
        } else {
            reconcileDatabaseWithMemory(); // if there are users in memory already, compare to database
        }
    }

    /**
     * Setup the memory space for the records
     */
    private void setupRecordDatabase() {
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
     */
    public void reconcileDatabaseWithMemory() {
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
     * Checks a ` credentials match in the database
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
     * @throws IOException
     */
    public void addEmergencyRecord(EmergencyRecord record) throws IOException {
        if (!this.getRecords().containsValue(record)) {
            this.getRecords().put(record.getMetadata().getTimeCreated(), record);
            log.info("Writing to database...");
            writeObject(records);
        }
    }

    private void writeObject(Object object) {
        try {
            outputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
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
        try {
            outputStream.writeObject(getUsers());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * Closes the streams to the database and any other cleanup
     * @throws IOException
     */
    public void closeDatabase() throws IOException {
        outputStream.close();
        inputStream.close();
    }

    /**
     * Retrieve the list of emergency records in the database
     * @return the list of records
     */
    public Map<Instant, EmergencyRecord> getRecords() {
        return records;
    }

    /**
     * Retrieve the list of users in the database
     * @return the list of users
     */
    public Map<String, EMSUser> getUsers() {
        return users;
    }

    /**
     * Attempt to retrieve records from database
     * @return the records on success, null on failure
     */
    public Map<Instant, EmergencyRecord> getDatabaseRecords() {
        try {
            return (Map<Instant, EmergencyRecord>) inputStream.readObject();
        } catch (EOFException e) {
            log.info("Nothing to read from database file.");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Attempt to retrieve users from database
     * @return the users on success, null on failure
     */
    public Map<String, EMSUser> getDatabaseUsers() {
        try {
            return (Map<String, EMSUser>) inputStream.readObject();
        } catch (EOFException e) {
            log.info("Nothing to read from database file.");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
