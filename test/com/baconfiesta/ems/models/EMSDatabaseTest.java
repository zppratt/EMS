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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
    private static final Path testDatabaseLocation = Paths.get("./db/test.db");

    /**
     * Test database file
     */
    private static final File mockFile = testDatabaseLocation.toFile();

    @Before
    public void setUp() throws Exception {
        database = EMSDatabase.getNewDatabase().withFile(mockFile);
        assertNotNull(database.getUsers());
        assertNotNull(database.getRecords());
    }

    @After
    public void tearDown() throws Exception {
        database.closeDatabase();
        Files.delete(testDatabaseLocation);
        assertNull(database.getUsers());
        assertNull(database.getRecords());
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

        database = EMSDatabase.getNewDatabase().withFile(mockFile); // Try to create a new database
        assertThat("Fresh test database was not created after deletion.", mockFile.exists(), is(true)); // Should succeed...
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
        database = EMSDatabase.getNewDatabase().withFile(mockFile); // Try to create a new database
        assertThat("Fresh test database was not created after deletion.", mockFile.exists(), is(true)); // Should succeed...
        assertNotNull(database);
        assertNotNull(database.getUsers());
        assertNotNull(database.getRecords());
    }

    @Test
    public void testVerifyUser() throws Exception {
        System.out.println("testVerifyUser");


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

    }

    @Test
    public void testAddUser() throws Exception {
        System.out.println("testAddUser");

        assertNotNull("Failure adding user to the database.", database.addUser("Bob", "Builder", "bbuilder", "password"));
        assertNotNull("Database memory contained no users: users memory object was null", database.getUsers());
        assertThat("User was not successfully added.", database.getUsers().containsKey("bbuilder"));
        assertNotNull("Database contained no users: database users object was null.", database.getDatabaseUsers());
        assertThat("User was not successfully added to the database.", database.getDatabaseUsers()
                .containsKey("bbuilder"));
    }

    @Test
    public void testLookupUser() throws Exception {

    }

    @Test
    public void testLookupEmergencyRecord() throws Exception {

    }

    @Test
    public void testRemoveUser() throws Exception {

    }

    @Test
    public void testCloseDatabase() throws Exception {

    }

    @Test
    public void testGetNewDatabase() throws Exception {

    }

    @Test
    public void testWithFile() throws Exception {

    }

    @Test
    public void testWithUsers() throws Exception {

    }

    @Test
    public void testWithRecords() throws Exception {

    }

    @Test
    public void testReconcileDatabaseWithMemory() throws Exception {

    }

    @Test
    public void testGetRecords() throws Exception {

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
        System.out.println(database.getDatabaseUsers().values());
        System.out.println(database.getUsers().values());

    }
}