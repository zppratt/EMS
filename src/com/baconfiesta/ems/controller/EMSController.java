package com.baconfiesta.ems.controller;

import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser;
import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;

import java.io.File;
import java.util.Date;

public class EMSController {

    private EMSUser currentUser;
    private EMSDatabase database;

    public EMSUser logIn(String username, String password) {

    }

    public void logOut() {

    }

    public EmergencyRecord createNewEmergency(String, String, String, String, String, String, int, String) {

    }

    private void determineNearestResponder(EmergencyRecord record) {

    }

    public void calculateRoute(EmergencyRecord record) {

    }

    public void finalizeRecord(EmergencyRecord record) {

    }

    public EmergencyRecord accessEmergencyRecord(String) {
        /* Why is it a string in parameter? Don't we need an instant?*/
    }

    public void generateReport(Date beginningDate, Date endingDate, File exportFile) {

    }

    public EMSUser[] getUsers() {

    }

    public EmergencyRecord[] getRecords() {

    }

    public void backupData(File backupFile) {

    }

    public void restoreData(File restoringFile) {

    }

}
