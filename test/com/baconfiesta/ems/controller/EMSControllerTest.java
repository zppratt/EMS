package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.TestConstants;
import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser.EMSUser;
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
import java.util.Random;

import static java.lang.Math.abs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
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

    EmergencyRecordBuilder erBuilder = EmergencyRecordBuilder.newBuilder();

    EmergencyRecord testRecord;

    EMSController controller;

    EMSAdminController adminController;

    EMSDatabase database;

    @Before
    public void setUp() throws Exception {
        database = new EMSDatabase(mockFile);
        controller = new EMSController(null, database);
        adminController = new EMSAdminController(null, database);
        testRecord = erBuilder.withTime(Instant.EPOCH).getNewEmergencyRecord(new EMSUser("","","","",true));
        controller.finalizeRecord(testRecord);
        controller.setUser(controller.getAdminUsers().get(0));
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
        System.out.println("logOut");

        controller.logOut();
        assertNull(controller.getCurrentUser());
    }

    @Test
    public void testCreateNewEmergency() throws Exception {
        System.out.println("testCreateNewEmergency");

        controller.determineNearestResponder(testRecord);
        controller.calculateRoute(testRecord);
        assertTrue(controller.getRecords().stream()
                .anyMatch(r -> r.getMetadata().getTimeCreated().equals(testRecord.getMetadata().getTimeCreated()))
        );
    }

    @Test
    public void testCalculateRoute() throws Exception {

    }

    @Test
    public void testFinalizeRecord() throws Exception {
        System.out.println("finalizeRecord");

        EmergencyRecord record = EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord(controller.getCurrentUser());
        assertFalse(controller.getRecords().stream()
                .anyMatch(r -> r.getMetadata().getTimeCreated().equals(record.getMetadata().getTimeCreated()))
        );
        controller.finalizeRecord(record);
        assertTrue(controller.getRecords().stream()
                .anyMatch(r -> r.getMetadata().getTimeCreated().equals(record.getMetadata().getTimeCreated()))
        );
    }

    @Test
    public void testAccessEmergencyRecord() throws Exception {
        System.out.println("accessEmergencyRecord");

        assertNotNull(controller.accessEmergencyRecord(Instant.EPOCH));

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
    public void testGetAdminUsers() throws Exception {
        System.out.println("getAdminUsers");

        adminController.addUser("Bilbo", "Baggins", "bbaggins", "bbaggins");
        adminController.setUserAdmin("bbaggins", true);
        assertThat(controller.getAdminUsers(), is(not(empty())));
    }

    @Test
    public void testGetRecords() throws Exception {
        System.out.println("getRecords");

        controller.finalizeRecord(testRecord);
        assertThat(controller.getRecords(), is(not(empty())));
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

    @Test
    public void testGetCurrentUser() throws Exception {
        System.out.println("getCurrentUser");

        EMSUser admin = adminController.addUser("Admin", "Admin", "admin", "Admin");
        adminController.setUserAdmin("admin", true);

        controller.setUser(admin);

        assertNotNull(controller.getCurrentUser());
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

    public void generateRecords() throws IOException, ClassNotFoundException {
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
                    .getNewEmergencyRecord(controller.getCurrentUser()));
        }
    }

}