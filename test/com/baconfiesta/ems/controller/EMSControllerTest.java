package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.TestConstants;
import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EmergencyRecord.Caller;
import com.baconfiesta.ems.models.EmergencyRecord.Category;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecordBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.abs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EMSDatabase.class})
public class EMSControllerTest implements TestConstants{

    final Path mockFilePath = Paths.get("db/test.db");

    final File mockFile = mockFilePath.toFile();

    EMSController controller;

    EMSAdminController adminController;

    EMSDatabase database;

    @Before
    public void setUp() throws Exception {
        database = new EMSDatabase(mockFile);
        controller = new EMSController(null, database);
        adminController = new EMSAdminController(null, database);
        // Default user created?
        assertNotNull(database.lookupUser(""));
    }

    @After
    public void tearDown() throws Exception {
        assertNotNull(database);
        database.closeDatabase();
        Files.delete(mockFilePath);
        assertNull(database.getCachedUsers());
        assertNull(database.getCachedRecords());
    }

    @Test
    public void testLogIn() throws Exception {
        System.out.println("testLogIn");

        // Test authentication failure
        assertNull(controller.logIn("bbaggins", "there and back again".toCharArray()));

        // Test authentication success
        database.addUser("Bilbo", "Baggins", "bbaggins", "there and back again");
        assertNotNull(controller.logIn("bbaggins", "there and back again".toCharArray()));
    }

    @Test
    public void testLogOut() throws Exception {

    }

    @Test
    public void testCreateNewEmergency() throws Exception {
        System.out.println("testCreateNewEmergency");


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
        adminController.addUser("Bilbo", "Baggins", "bbaggins", "bbaggins");
        adminController.addUser("Frodo", "Baggins", "fbaggins", "fbaggins");
        adminController.addUser("Samwise", "Gamgee", "sgamgee", "sgamgee");
        assertThat(controller.getUsers().size(), is(3));
        // Test failure
        adminController.removeUser("bbaggins");
        adminController.removeUser("fbaggins");
        adminController.removeUser("sgamgee");
        // At this point, only the admin user is present, and 'getUsers' only returns non-admins at this point
        assertTrue(controller.getUsers().isEmpty());
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
    public void testGetAdminUsers() throws Exception {

    }

    @Test
    public void testAuthenticateUser() throws Exception {

    }

    @Test
    public void testSetUser() throws Exception {

    }

    @Test
    public void testGetCurrentUser() throws Exception {

    }

    @Test
    public void testGetRecentRecords() throws Exception {
        System.out.println("getUsers");

        generateRecords();
        EmergencyRecord[] list = controller.getRecentRecords();

        for (EmergencyRecord emergencyRecord : list) {
            System.out.println(emergencyRecord);
            assertTrue(emergencyRecord!=null);
        }

    }

    private void generateRecords() throws IOException, ClassNotFoundException {
        Random r = new Random(Instant.now().toEpochMilli());
        long endTime = Timestamp.valueOf("3000-01-01 00:00:00").getTime();
        for ( int i = 0; i < 100; i++ ) {
            controller.finalizeRecord(EmergencyRecordBuilder.newBuilder()
                    .withCaller(new Caller(
                            firstNames[abs(r.nextInt()%(firstNames.length-1))],
                            lastNames[abs(r.nextInt()%(lastNames.length-1))],
                            "999-999-9999"))
                    .withCategory(Category.HOAX)
                    .withTime(Instant.ofEpochMilli((long) ((Math.random() * endTime))))
                    .getNewEmergencyRecord());
        }
    }

}