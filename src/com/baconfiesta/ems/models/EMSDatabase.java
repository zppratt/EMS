package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.time.Instant;
import java.util.HashMap;
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
    private static HashMap<String, EMSUser> users;

    /**
     * List of emergency records
     * key: the creation time
     * value: the emergency record
     */
    private static HashMap<Instant, EmergencyRecord> records;

    /**
     * Default constructor for a database object
     */
    public EMSDatabase() {
        // Check if the streams for the database reading and writing have been created. If not, create them.
        setupStreams();
        // Setup logger
        setupLogger();
        // Check if the user and records are initialized in memory
        setupUserDatabase();
        setupRecordDatabase();
    }

    /**
     * Check input and output streams for setup
     */
    private void setupStreams() {
        if (outputStream==null) {
            if (fileOutputStream==null) {
                try {
                    File directory = new File("./db");
                    if (!(directory.isDirectory())) {
                        directory.mkdir();
                    }
                    fileOutputStream = new FileOutputStream(database);
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
            logger.info("Wow! I'm configured!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setup the memory space for the user
     */
    private void setupUserDatabase() {
        if (users != null) {
            try {
                // Does it exist in the database file? Pull it into memory
                users = (HashMap<String, EMSUser>) inputStream.readObject();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        // Otherwise created a new one
        users = new HashMap<>();
    }

    /**
     * Setup the memory space for the records
     */
    private void setupRecordDatabase() {
        if (users != null) {
            try {
                // Does it exist in the database file? Pull it into memory
                records = (HashMap<Instant, EmergencyRecord>) inputStream.readObject();
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (EOFException e) {

            }  catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Otherwise created a new one
        records = new HashMap<>();
    }

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
