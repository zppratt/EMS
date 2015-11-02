package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSUser;

public class EMSAdminController extends EMSController {

    public EMSUser addUser(String firstname, String lastname, String username, String password) {
        /* Added the password argument because how else can we define a password to a user? */
    }

    public boolean removeUser(EMSUser user) {

    }

    public EMSUser lookupUser(String username) {

    }

    public String viewUserActivity(EMSUser user) {

    }

    public void setUser(EMSUser user) {
        /* What is set user for again? Is it set admin? */
    }
}
