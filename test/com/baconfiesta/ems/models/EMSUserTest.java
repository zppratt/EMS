package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EMSUser.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecordBuilder;
import com.baconfiesta.ems.models.EmergencyRecord.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.time.Instant;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EMSUserTest {

    private EMSUser regularUser;
    private EMSUser adminUser;

    @Before
    public void setUp() throws Exception {
        // Test regular user
        regularUser = new EMSUser("Frodo", "Baggins", "fbaggins", "password", false);
        // Test admin
        adminUser = new EMSUser("Samwise", "Gamgee", "sgamgee", "password", true);
    }

    @Test
    public void testCheckPassword() throws Exception {
        System.out.println("testCheckPassword");

        // Test regular user failure
        assertFalse(regularUser.checkPassword("MOUNT DOOOOOM".toCharArray()));
        // Test regular user success
        assertTrue(regularUser.checkPassword("password".toCharArray()));
        // Test admin failure
        assertFalse(adminUser.checkPassword("MOUNT DOOOOOM".toCharArray()));
        // Test admin success
        assertTrue(adminUser.checkPassword("password".toCharArray()));
    }

    @Test
    public void testGetRecords() throws Exception {
        System.out.println("testGetRecords");

        // Test failure
        assertThat(regularUser.getRecords().values(), is(empty()));
        // Test success
        Whitebox.setInternalState(regularUser, "records", createTestRecords());
        assertThat(regularUser.getRecords().values(), is(not(empty())));
    }

    private HashMap<Instant, EmergencyRecord> createTestRecords() {
        Metadata m = new Metadata();
        Whitebox.setInternalState(m, "timeCreated", Instant.EPOCH);
        HashMap<Instant, EmergencyRecord> records = new HashMap<>();
        records.put(Instant.EPOCH, EmergencyRecordBuilder.newBuilder()
                .withMetadata(m)
                .getNewEmergencyRecord(adminUser));
        records.put(Instant.EPOCH, EmergencyRecordBuilder.newBuilder()
                .withMetadata(m)
                .getNewEmergencyRecord(adminUser));
        records.put(Instant.EPOCH, EmergencyRecordBuilder.newBuilder()
                .withMetadata(m)
                .getNewEmergencyRecord(adminUser));
        return records;
    }

    @Test
    public void testIsAdmin() throws Exception {
        System.out.println("testIsAdmin");

        // Test failure
        assertFalse(regularUser.isAdmin());

        // Test success
        assertTrue(adminUser.isAdmin());
    }

    @Test
    public void testGetFirstname() throws Exception {
        System.out.println("getFirstName");

        assertEquals("Frodo", regularUser.getFirstname());
    }

    @Test
    public void testGetLastname() throws Exception {
        System.out.println("getLastName");

        assertEquals("Baggins", regularUser.getLastname());
    }

    @Test
    public void testGetUsername() throws Exception {
        System.out.println("getUsername");

        assertEquals("fbaggins", regularUser.getFirstname());
    }

    @Test
    public void testSetAdmin() throws Exception {
        System.out.println("setAdmin");

        regularUser.setAdmin(true);
        assertTrue(regularUser.isAdmin());
    }

    @Test
    public void testAddRecord() throws Exception {
        System.out.println("addRecord");

        EmergencyRecord testRecord = EmergencyRecordBuilder.newBuilder()
                .withTime(Instant.EPOCH)
                .getNewEmergencyRecord(new EMSUser("","","","",true));
        regularUser.addRecord(testRecord);
        assertTrue(regularUser.getRecords().containsValue(testRecord));
    }
}