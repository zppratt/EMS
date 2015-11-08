package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EMSDatabase.class})
public class EMSControllerTest {

    private EMSController controller;

    @Before
    public void setUp() throws Exception {
        controller = new EMSController(new EMSUser("John", "Doe", "jdoe", "jdoe", false),
                EMSDatabase.getNewDatabase().withFile(new File("test.db")));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLogIn() throws Exception {
        EMSUser user = controller.logIn("jdoe", "jdoe");
        assertThat(user, is(not(equals(null))));
    }

    @Test
    public void testLogOut() throws Exception {

    }

    @Test
    public void testCreateNewEmergency() throws Exception {

    }

    @Test
    public void testCalculateRoute() throws Exception {

    }

    @Test
    public void testFinalizeRecord() throws Exception {

    }

    @Test
    public void testAccessEmergencyRecord() throws Exception {

    }

    @Test
    public void testGenerateReport() throws Exception {

    }

    @Test
    public void testGetUsers() throws Exception {

    }

    @Test
    public void testGetRecords() throws Exception {

    }

    @Test
    public void testBackupData() throws Exception {

    }

    @Test
    public void testRestoreData() throws Exception {

    }

    @Test
    public void testAuthenticateUser() throws Exception {

    }

    @Test
    public void testSetUser() throws Exception {

    }
}