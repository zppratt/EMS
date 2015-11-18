package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser.EMSUser;

import java.io.IOException;

public class EMSSandboxController extends EMSController {

    EMSDatabase database;
    EMSUser user;

    public EMSSandboxController() throws IOException, ClassNotFoundException {

        user = new EMSUser("","","","",false);



    }

}
