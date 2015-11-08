package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser;

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
    public EMSAdminController(EMSUser user, EMSDatabase database) {
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
    public EMSUser addUser(String firstname, String lastname, String username, String password) {
        return new EMSUser(firstname,lastname,username,password,false);
    }

    /**
     * Removes a user from the system
     * @param user the user
     * @return whether the removal was successful or not
     */
    public boolean removeUser(EMSUser user) {
        return false;
    }

    /**
     * Get a user from the system by name
     * @param username the username
     * @return the User if found, otherwise null
     */
    public EMSUser lookupUser(String username) {
        return null;
    }

    /**
     * View a users activity
     * @param user the user
     * @return the activity of the user
     */
    public String viewUserActivity(EMSUser user) {
        return null;
    }
}
