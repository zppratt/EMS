package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecordBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EMSDatabase.class})
public class EMSControllerTest {

    private final Path mockFilePath = Paths.get("db/test.db");

    private final File mockFile = mockFilePath.toFile();

    private EMSController controller;

    private EMSAdminController adminController;

    private EMSDatabase database;

    @Before
    public void setUp() throws Exception {
        database = new EMSDatabase(mockFile);
        controller = new EMSController(null, database);
        adminController = new EMSAdminController(null, database);
        // Default user created?
        assertNotNull(database.lookupUser("admin"));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLogIn() throws Exception {
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
        System.out.println("testFinalizeRecord");

        EmergencyRecord record = EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord();
        assertFalse(Arrays.asList(controller.getRecords()).contains(record));
        controller.finalizeRecord(record);
        assertTrue(Arrays.asList(controller.getRecords()).contains(record));
    }

    @Test
    public void testAccessEmergencyRecord() throws Exception {

    }

    @Test
    public void testGenerateReport() throws Exception {

    }

    @Test
    public void testGetUsers() throws Exception {
        System.out.println("testGetUsers");

        // Test success
        assertThat(controller.getUsers().length, is(not(0)));
        // Test failure
        assertTrue(adminController.removeUser("admin"));
        assertThat(controller.getUsers().length, is(0));
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
        System.out.println("testAuthenticateUser");

        // Test authentication failure
        assertNull(controller.authenticateUser("bbaggins", "there and back again"));

        // Test authentication success
        database.addUser("Bilbo", "Baggins", "bbaggins", "there and back again");
        assertNotNull(controller.authenticateUser("bbaggins", "there and back again"));

    }
}