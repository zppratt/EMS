package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSUser.EMSUser;

import java.io.IOException;

public class Authenticator extends EMSController {

    public Authenticator() throws Exception {
    }

    public static EMSUser authenticate(String username, char[] password) throws
            NullPointerException {
        return authenticateUser(username, password);
    }

    public static void init() throws IOException, ClassNotFoundException {
        new EMSController();
    }

}
