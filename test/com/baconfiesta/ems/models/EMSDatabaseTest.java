package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import com.baconfiesta.ems.models.EmergencyRecord.Metadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

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

    /**
     * Output stream for storing the users and records
     */
    private static ObjectOutputStream outputStream;

    /**
     * Input stream for receiving the users and records
     */
    private static ObjectInputStream inputStream;

    /**
     * File output stream for the database
     */
    private static FileOutputStream fileOutputStream;

    /**
     * File input stream for the database
     */
    private static FileInputStream fileInputStream;

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

        mockFile.delete(); // Delete the file
        assertThat("Test database was not deleted.", mockFile.exists(), is(false)); // Should succeed...

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

    /**
     * Test the reading and writing of a file
     */
    @Test
    public void testDatabaseReadFile() {

    }

    @Test
    public void testVerifyUser() throws Exception {

    }

    /**
     * Test of the addEmergencyRecord method
     */
    @Test
    public void testAddEmergencyRecord() throws Exception {
//        File mockDirectory = createMock(File.class);
//        expectNew(File.class, "test").andReturn(mockDirectory);
//        File mockFile = createMock(File.class);
//        expectNew(File.class, "test").andReturn(mockFile);
//        fileOutputStream = createNiceMock(FileOutputStream.class);
//        expectNew(FileOutputStream.class, mockFile).andReturn(fileOutputStream);
        HashMap<Instant, EmergencyRecord> mockRecords = new HashMap<>();
//        expectPrivate(database, "writeObject", mockRecords).anyTimes();
        EmergencyRecord mockRecord = new EmergencyRecord();
        mockRecords.put(Instant.EPOCH, mockRecord);
        replayAll();
        database.addEmergencyRecord(mockRecord);
        verifyAll();
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

    /**
     * Test of the write
     */

}