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
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
        assertNull(database.getUsers());
        assertNull(database.getRecords());
    }

    /**
     * Test the creation of a database object if no database file exists
     */
    @Test
    public void testDatabaseNoFile() throws IOException {
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
    public void testDatabaseWithFile() throws IOException {
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

        // Test that the records object gets updated
        assertThat("New records object was not empty", database.getRecords().isEmpty());
        EmergencyRecord testRecord = EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord();
        database.addEmergencyRecord(testRecord);
        assertThat("New record was not added.", database.getRecords().containsValue(testRecord));
    }

    @Test
    public void testGetEmergencyRecord() throws Exception {

    }

    @Test
    public void testAddUser() throws Exception {

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

    }

    @Test
    public void testGetDatabaseRecords() throws Exception {

    }

    @Test
    public void testGetDatabaseUsers() throws Exception {

    }
}