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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
        assertFalse(regularUser.checkPassword("MOUNT DOOOOOM"));
        // Test regular user success
        assertTrue(regularUser.checkPassword("password"));
        // Test admin failure
        assertFalse(adminUser.checkPassword("MOUNT DOOOOOM"));
        // Test admin success
        assertTrue(adminUser.checkPassword("password"));
    }

    @Test
    public void testGetRecords() throws Exception {
        System.out.println("testGetRecords");

        // Test failure
        assertNull(regularUser.getRecords());
        // Test success
        Whitebox.setInternalState(regularUser, "records", createTestRecords());
        assertNotNull(regularUser.getRecords());
    }

    private HashMap<Instant, EmergencyRecord> createTestRecords() {
        Metadata m = new Metadata();
        Whitebox.setInternalState(m, "timeCreated", Instant.EPOCH);
        HashMap<Instant, EmergencyRecord> records = new HashMap<>();
        records.put(Instant.EPOCH, EmergencyRecordBuilder.newBuilder().withMetadata(m).getNewEmergencyRecord());
        records.put(Instant.EPOCH, EmergencyRecordBuilder.newBuilder().withMetadata(m).getNewEmergencyRecord());
        records.put(Instant.EPOCH, EmergencyRecordBuilder.newBuilder().withMetadata(m).getNewEmergencyRecord());
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
}