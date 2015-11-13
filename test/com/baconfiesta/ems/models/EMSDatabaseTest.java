package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecordBuilder;
import com.baconfiesta.ems.models.EmergencyRecord.Metadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Metadata.class, EMSDatabase.class, EmergencyRecord.class, Metadata.class} )
public class EMSDatabaseTest {

    /**
     * Class under test
     */
    EMSDatabase database;

    /**
     * String path to test database
     */
    private static final Path mockDatabaseLocation = Paths.get("./db/test.db");

    /**
     * Test database file
     */
    private static final File mockFile = mockDatabaseLocation.toFile();

    @Before
    public void setUp() throws Exception {
        database = new EMSDatabase(mockFile);
        assertNotNull(database);
        assertNotNull(database.getUsers());
        assertNotNull(database.getRecords());
        assertTrue(database.isOpen());
    }

    @After
    public void tearDown() throws Exception {
        assertNotNull(database);
        database.closeDatabase();
        Files.delete(mockDatabaseLocation);
        assertNull(database.getUsers());
        assertNull(database.getRecords());
        assertFalse(database.isOpen());
    }

    /**
     * Test the creation of a database object if no database file exists
     */
    @Test
    public void testDatabaseNoFile() throws IOException, ClassNotFoundException {
        System.out.println("testDatabaseNoFile");

        assertThat("Test database was not created on setup.", mockFile.exists(), is(true)); // Make sure the file exists
        database.closeDatabase(); // Open access to the test database file by closing the database object

        assertThat("Test database was not deleted.", mockFile.delete(), is(true)); // Should succeed...

        database = new EMSDatabase(mockFile); // Try to create a new database
        assertThat("Fresh test database was not created after deletion.", mockFile.exists(), is(true)); // Should succeed...

        // Is the default user present?
        assertNotNull(database.lookupUser("admin"));

        assertNotNull(database);
        assertNotNull(database.getUsers());
        assertNotNull(database.getRecords());
    }

    /**
     * Test the creation of a database object if database file exists prior to creation
     */
    @Test
    public void testDatabaseWithFile() throws IOException, ClassNotFoundException {
        System.out.println("testDatabaseWithFile");

        // Basically just created another database object while the last one has already created the file
        assertThat("Test database was not created on setup.", mockFile.exists(), is(true)); // Make sure the file exists
        database.closeDatabase(); // Open access to the test database file by closing the database object
        database = new EMSDatabase(mockFile); // Try to create a new database
        assertThat("Fresh test database was not created after deletion.", mockFile.exists(), is(true)); // Should succeed...
        assertNotNull(database);
        assertNotNull(database.getUsers());
        assertNotNull(database.getRecords());
    }

    @Test
    public void testVerifyUser() throws Exception {
        System.out.println("testVerifyUser");

        // Fail to verify a user
        database.addUser("Frodo", "Baggins", "fbaggins", "password");
        assertNull(database.verifyUser("fbaggins", "HEHE I'M TRYING TO STEAL YOUR ACCOUNT"));

        // Succeed in verifying a user
        assertNotNull(database.verifyUser("fbaggins", "password"));

    }

    @Test
    public void testAddEmergencyRecord() throws Exception {
        System.out.println("testAddEmergencyRecord");

        // Test that the records object gets updated in memory and database
        assertTrue("New records object was not empty", database.getRecords().isEmpty());
        EmergencyRecord testRecord = EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord();
        database.addEmergencyRecord(testRecord);
        assertTrue("New record was not added.", database.getRecords().containsValue(testRecord));

    }

    @Test
    public void testGetEmergencyRecord() throws Exception {
        System.out.println("testGetEmergencyRecord");

        // Test failed retrieval by putting a current record and trying to find one made at the EPOCH
        database.addEmergencyRecord(EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord());
        assertNull(database.getEmergencyRecord(Instant.EPOCH));

        // Now put one made at the EPOCH and try to get it... should succeed
        Metadata m = new Metadata();
        Whitebox.setInternalState(m, "timeCreated", Instant.EPOCH);
        database.addEmergencyRecord(EmergencyRecordBuilder.newBuilder().withMetadata(m).getNewEmergencyRecord());
        assertNotNull(database.getEmergencyRecord(Instant.EPOCH));
    }

    @Test
    public void testAddUser() throws Exception {
        System.out.println("testAddUser");

        assertNotNull("Failure adding user to the database.", database.addUser("Bob", "Builder", "bbuilder", "password"));
        assertNotNull("Database memory contained no users: users memory object was null", database.getUsers());
        assertTrue("User was not successfully added.", database.getUsers().containsKey("bbuilder"));
        assertNotNull("Database contained no users: database users object was null.", database.getDatabaseUsers());
        assertTrue("User was not successfully added to the database.", database.getDatabaseUsers().containsKey("bbuilder"));
    }

    @Test
    public void testLookupUser() throws Exception {
        System.out.println("testLookupUser");

        // Test failure to find a user
        assertNull(database.lookupUser("fbaggins"));

        // Test found a user
        database.addUser("Frodo", "Baggins", "fbaggins", "password");
        assertNotNull(database.lookupUser("fbaggins"));
    }

    @Test
    public void testLookupEmergencyRecord() throws Exception {
        System.out.println("testLookupEmergencyRecord");

        // Test failure to find a record
        assertNull(database.lookupEmergencyRecord(Instant.EPOCH));

        // Test found a user
        Metadata m = new Metadata();
        Whitebox.setInternalState(m, "timeCreated", Instant.EPOCH);
        database.addEmergencyRecord(EmergencyRecordBuilder.newBuilder().withMetadata(m).getNewEmergencyRecord());
        assertNotNull(database.lookupEmergencyRecord(Instant.EPOCH));
    }

    @Test
    public void testRemoveUser() throws Exception {
        System.out.println("testRemoveUser");

        // Test a correct removal
        database.addUser("Frodo", "Baggins", "fbaggins", "password");
        database.removeUser("fbaggins");
        assertFalse(database.getUsers().containsKey("fbaggins"));

        // Test for a failed removal
        database.addUser("Frodo", "Baggins", "fbaggins", "password");
        database.removeUser("fbaggin");
        assertTrue(database.getUsers().containsKey("fbaggins"));
    }

    @Test
    public void testGetRecords() throws Exception {
        System.out.println("testGetRecords");

        assertNotNull(database.getRecords());
    }

    @Test
    public void testGetUsers() throws Exception {
        System.out.println("testGetUsers");

        assertNotNull(database.getUsers());
    }

    @Test
    public void testGetDatabaseRecords() throws Exception {
        System.out.println("testGetDatabaseRecords");

        // Add some records
        database.addEmergencyRecord(EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord());
        database.addEmergencyRecord(EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord());
        database.addEmergencyRecord(EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord());
        // Are there records?
        assertNotNull(database.getRecords());
    }

    @Test
    public void testGetDatabaseUsers() throws Exception {
        System.out.println("testGetDatabaseUsers");

        // Add some users
        database.addUser("Paul","Ron","rpaul","rpaul");
        database.addUser("Trump","Donald","dtrump","dtrump");
        database.addUser("Clinton","Hilary","hclinton","hclinton");
        // Are there users?
        assertNotNull("No users are in memory", database.getUsers());
        assertNotNull("No users are in the database.", database.getDatabaseUsers());

        // Debugging...
//        System.out.println("\nDatabase file: " +database.getDatabaseUsers().values());
//        System.out.println("In memory: " + database.getUsers().values());

    }
}