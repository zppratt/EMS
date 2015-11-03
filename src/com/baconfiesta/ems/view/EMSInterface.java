package com.baconfiesta.ems.view;

import com.baconfiesta.ems.controller.EMSController;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;

import javax.swing.*;

/**
 * The main user interface window of the EMS system.
 * @author team_bacon_fiesta
 */
public class EMSInterface {

    private EMSController controller;
    private JPanel header;
    private JPanel sidebar;
    private JPanel mainframe;
    private JPanel footer;
    private EmergencyRecord tempFile;
    private EmergencyRecord[] recentRecords;

    /**
     * Show log in screen
     */
    public void logIn() {

    }

    /**
     * Show screen for user actions
     */
    private void userActions() {

    }

    /**
     * Show screen to enter info for an emergency record
     */
    private void enterInfo() {

    }

    /**
     * Show screen for the user to select the route from responder to the emergency
     */
    private void routeSelection() {

    }

    /**
     * Show a summary of the emergency record to finalize it or cancel
     */
    private void summaryView() {

    }

    /**
     * Show the screen to generate some statistics about the emergency records
     */
    private void generateStats() {

    }

    /**
     * Show the screen to view the emergency records
     */
    private void displayRecords() {

    }

    /**
     * Admin only: Show the screen to add and remove users
     */
    private void manageUsers() {

    }

    /**
     * Admin only: Show the screen to backup and restore data
     */
    private void manageData() {

    }

    /**
     * Admin only: Show the screen to view the activity of a system user
     */
    private void viewUserActivity() {

    }

    /**
     * Admin only: Show the screen to modify emergency records
     */
    private void modifyRecords() {

    }

}