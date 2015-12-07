package com.baconfiesta.ems.controller;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EMSAdminControllerTest extends EMSControllerTest {

    @Test
    public void testAddUser() throws Exception {
        System.out.println("addUser");

        assertEquals(1, controller.getUsers().size());
        adminController.addUser("Frodo", "Baggins", "fbaggins", "fbaggins");
        assertEquals(2, controller.getUsers().size());
    }

    @Test
    public void testRemoveUser() throws Exception {
        System.out.println("removeUser");

        adminController.addUser("Bilbo", "Baggins", "bbaggins", "bbaggins");
        adminController.removeUser("bbaggins");
        assertTrue(controller.getUsers().isEmpty());
    }

    @Test
    public void testLookupUser() throws Exception {
        System.out.println("lookupUser");

        adminController.addUser("Bilbo", "Baggins", "bbaggins", "bbaggins");
        assertNotNull("User was not successfully found in lookup.", adminController.lookupUser("bbaggins"));
    }

    @Test
    public void testSetUserAdmin() throws Exception {
        System.out.println("setUserAdmin");

        assertThat(controller.getAdminUsers().size(), is(1)); // Default user
        adminController.addUser("Bilbo", "Baggins", "bbaggins", "bbaggins");
        adminController.setUserAdmin("bbaggins", true);
        assertThat(controller.getAdminUsers().size(), is(2)); // Default user
    }

    @Test
    public void testRemoveRecord() throws Exception {
        System.out.println("removeRecord");

        controller.finalizeRecord(testRecord);
        assertTrue(controller.getRecords().stream()
                .anyMatch(p -> p.getMetadata().getTimeCreated().equals(testRecord.getMetadata().getTimeCreated())
                )
        );
        adminController.removeRecord(testRecord);
        assertFalse(controller.getRecords().stream()
                .anyMatch(p -> p.getMetadata().getTimeCreated().equals(testRecord.getMetadata().getTimeCreated())
                )
        );
    }
}