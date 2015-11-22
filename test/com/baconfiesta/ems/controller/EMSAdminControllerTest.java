package com.baconfiesta.ems.controller;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EMSAdminControllerTest extends EMSControllerTest {

    @Test
    public void testAddUser() throws Exception {

        assertTrue(controller.getUsers().isEmpty());

        adminController.addUser("Bilbo", "Baggins", "bbaggins", "bbaggins");

        assertThat(controller.getUsers().size(), is(1));

    }

    @Test
    public void testRemoveUser() throws Exception {

        assertTrue(controller.getUsers().isEmpty());

        adminController.addUser("Bilbo", "Baggins", "bbaggins", "bbaggins");

        assertThat(controller.getUsers().size(), is(1));

        adminController.removeUser("bbaggins");

        assertTrue(controller.getUsers().isEmpty());

    }

    @Test
    public void testLookupUser() throws Exception {

        assertTrue(controller.getUsers().isEmpty());

        adminController.addUser("Bilbo", "Baggins", "bbaggins", "bbaggins");

        assertThat(controller.getUsers().size(), is(1));

        assertNotNull("User was not successfully found in lookup.", adminController.lookupUser("bbaggins"));

    }

    @Test
    public void testSetUserAdmin() throws Exception {

        assertThat(controller.getAdminUsers().size(), is(1)); // Default user

        adminController.addUser("Bilbo", "Baggins", "bbaggins", "bbaggins");

        adminController.setUserAdmin("bbaggins", true);

        assertThat(controller.getAdminUsers().size(), is(2)); // Default user

    }

    @Test
    public void testViewUserActivity() throws Exception {

    }
}