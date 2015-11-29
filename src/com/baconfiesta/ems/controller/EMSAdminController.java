package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.*;

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
        long endTime = Timestamp.valueOf("2015-01-01 00:00:00").getTime();
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
        for (int i = 0; i < 100; i++) {
            EmergencyRecord er = EmergencyRecordBuilder.newBuilder()
                    .withCaller(new Caller(
                            firstNames[abs(r.nextInt() % (firstNames.length - 1))],
                            lastNames[abs(r.nextInt() % (lastNames.length - 1))],
                            "999-999-9999"
                    ))
                    .withLocation(new Location(
                            "4000 Parnell Ave",
                            "Fort Wayne", "Indiana"
                    ))
                    .withResponder(new Responder(
                            "999-999-9999",
                            "2101 E Coliseum Blvd",
                            "Fort Wayne", "Indiana"
                    ))
                    .withDescription(
                            "Some really bad stuff is happening." +
                            "Some really bad stuff is happening." +
                            "Some really bad stuff is happening."
                    )
                    .withCategory(Category.HOAX)
                    .withTime(Instant.ofEpochMilli((long) ((Math.random() * endTime))))
                    .getNewEmergencyRecord(getUsers().get((int)(Math.random() * (getUsers().size()-1))));
            finalizeRecord(er);
            System.out.println(er);
        }
    }

}
