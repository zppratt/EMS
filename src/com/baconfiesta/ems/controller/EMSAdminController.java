package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSUser;

/**
 * Privileged version of the main controller for the EMS system
 */
public class EMSAdminController extends EMSController {

    /**
     * Adds a user to the system
     * @param firstname
     * @param lastname
     * @param username
     * @param password
     * @return the User on success, null on failure
     */
    public EMSUser addUser(String firstname, String lastname, String username, String password) {
        /* Added the password argument because how else can we define a password to a user? */
    }

    /**
     * Removes a user from the system
     * @param user
     * @return whether the removal was successful or not
     */
    public boolean removeUser(EMSUser user) {

    }

    /**
     * Get a user from the system by name
     * @param username
     * @return the User if found, otherwise null
     */
    public EMSUser lookupUser(String username) {

    }

    /**
     * View a users activity
     * @param user
     * @return the activity of the user
     */
    public String viewUserActivity(EMSUser user) {

    }

    /**
     * Sets the current user
     * @param user
     */
    public void setUser(EMSUser user) {

    }
}
