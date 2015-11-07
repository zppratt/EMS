package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import com.baconfiesta.ems.models.EmergencyRecord.Metadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.HashMap;

import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Metadata.class, EMSDatabase.class, EmergencyRecord.class, Metadata.class} )
public class EMSDatabaseTest {

    /**
     * Class under test, sometimes mocked, sometimes a real object
     */
    EMSDatabase database;

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
        database = new EMSDatabase();
    }

    @After
    public void tearDown() throws Exception {
        database.closeDatabase();
    }

    /**
     * Test the opening and closing of a database
     */
    @Test
    public void testDatabaseOpenAndClose() {
        database = new EMSDatabase();
        try {
            database.closeDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the creation of a database if no db file exists
     */
    @Test
    public void testDatabaseNoDirectory() throws Exception {
//        File fileMock = PowerMock.createMock(File.class);
//
//        // Creation of the database
//        expectNew(File.class, "./db").andReturn(fileMock);
//        // Creation of the database file
//        expectNew(File.class, "./db/database.db").andReturn(fileMock);
//
//        replayAll();
//        database = new EMSDatabase();
//        verifyAll();
//        // Close the database
//        try {
//            database.closeDatabase();
//            expectLastCall().once();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Test the creation of a database if no db file exists
     */
    @Test
    public void testDatabaseNoFile() {
        EMSDatabase db = new EMSDatabase();
        try {
            db.closeDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the writing of a file
     */
    @Test
    public void testDatabaseWriteFile() {
        // Write a dummy object

        // Try to read the dummy object
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