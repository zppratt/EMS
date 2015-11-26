package com.baconfiesta.ems;

import com.baconfiesta.ems.controller.EMSAdminController;
import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser.EMSUser;
import org.junit.Test;

import java.io.IOException;

public class GenerateTestData {

    EMSAdminController ac;

    @Test
    public void testGenerateData() throws IOException, ClassNotFoundException, InterruptedException {
        ac = new EMSAdminController(new EMSUser("","","","",true),new EMSDatabase());
        ac.generateTestData();
    }
}