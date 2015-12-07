package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.*;
import javassist.tools.rmi.ObjectNotFoundException;

import javax.swing.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;

import static java.lang.Math.abs;

/**
 * Privileged version of the main controller for the EMS system
 * @author team_bacon_fiesta
 */
public class EMSAdminController extends EMSController {

    /**
     * Default constructor for a user controller
     *
     * @param user the user to use the controller
     * @param database the database
     */
    public EMSAdminController(EMSUser user, EMSDatabase database) throws IOException, ClassNotFoundException, InterruptedException {
        super(user, database);
    }

    /**
     * Adds a user to the system
     * @param firstname the user's first name
     * @param lastname the user's last name
     * @param username the user's username
     * @param password the user's password
     * @return the User on success, null on failure
     */
    public EMSUser addUser(String firstname, String lastname, String username, String password) throws IOException, ClassNotFoundException {
        return _database.addUser(firstname, lastname, username, password);
    }

    /**
     * Removes a user from the system
     * @param username the username of the user to remove
     * @return whether the removal was successful or not
     */
    public boolean removeUser(String username) throws IOException, ClassNotFoundException {
        return _database.removeUser(username);
    }

    public boolean removeRecord(EmergencyRecord record) throws IOException {
        return _database.removeRecord(record);
    }

    /**
     * Get a user from the system by name
     * @param username the username
     * @return the User if found, otherwise null
     */
    public EMSUser lookupUser(String username) throws IOException, ClassNotFoundException {
        return _database.lookupUser(username);
    }

    public void setUserAdmin(String username, boolean admin) throws IOException {
        _database.setUserAdmin(username, admin);
    }

    /**
     * View a users activity
     * @param user the user
     * @return the activity of the user
     */
    public String viewUserActivity(EMSUser user) {
        return null;
    }

    /**
     * Creates users and records for manual testing
     */
    public void generateTestData() throws IOException, ClassNotFoundException {
        Random r = new Random(Instant.now().toEpochMilli());
        long endTime = Timestamp.valueOf("2017-01-01 00:00:00").getTime();
        System.out.println("Generating Users:");
        for (int i = 0; i < 15; i++) {
            String firstName = firstNames[abs(r.nextInt() % (firstNames.length - 1))];
            String lastName = lastNames[abs(r.nextInt() % (lastNames.length - 1))];
            String id = firstName.substring(0,1).toLowerCase() + lastName.toLowerCase();
            EMSUser usr = addUser(firstName, lastName, id, id);
            if (Math.random() > .7) {
                setUserAdmin(usr.getUsername(), true);
            }
            System.out.println(usr + ":" + (usr.isAdmin() ? "admin" : "not admin"));
        }
        System.out.println("Generating Records:");
        for (int i = 0; i < 25; i++) {
            EmergencyRecordBuilder builder = EmergencyRecordBuilder.newBuilder()
                    .withCaller(new Caller(
                            firstNames[abs(r.nextInt() % (firstNames.length - 1))],
                            lastNames[abs(r.nextInt() % (lastNames.length - 1))],
                            "999-999-9999"
                    ))
                    .withLocation(new Location(
                            randomStreet(),
                            "Indiana", "Fort Wayne"
                    ))
                    .withResponder(new Responder(
                            "999-999-9999",
                            randomStreet(),
                            "Indiana", "Fort Wayne"
                    ))
                    .withDescription(
                            "Some really bad stuff is happening." +
                            "Some really bad stuff is happening." +
                            "Some really bad stuff is happening."
                    )
                    .withCategory(randomCategory())
                    .withTime(Instant.ofEpochMilli((long) ((Math.random() * endTime))));
            EmergencyRecord record = builder
                    .getNewEmergencyRecord(getUsers().get((int)(Math.random() * (getUsers().size()-1))));
            try {
                calculateRoute(record, false);
            } catch(ObjectNotFoundException e) {
                e.printStackTrace();
            }
            finalizeRecord(record);
            System.out.println(record);
        }
    }

    Category randomCategory() {
        int pick = new Random().nextInt(Category.values().length);
        return Category.values()[pick];
    }

    String randomStreet() {
        String[] streets = {
                "9030 ima Rd",
                "3014 N Clinton St",
                "3927 E State Blvd",
                "3208 Coliseum Blvd",
                "4230 W Jefferson Blvd",
                "11321 Aboite Center Rd"
        };
        int pick = new Random().nextInt(streets.length);
        return streets[pick];
    }

}
