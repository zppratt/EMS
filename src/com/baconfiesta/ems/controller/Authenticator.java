package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSUser.EMSUser;

import java.io.IOException;

public class Authenticator extends EMSController {

    public Authenticator() throws IOException, ClassNotFoundException {
    }

    public static EMSUser authenticate(String username, char[] password) throws
            NullPointerException, IOException, ClassNotFoundException {
        return authenticateUser(username, password);
    }

    public static void init() throws IOException, ClassNotFoundException {
        new EMSController();
    }

}
