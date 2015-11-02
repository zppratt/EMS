package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;

import java.time.Instant;
import java.util.HashMap;

public class EMSDatabase {

    private HashMap<String, EMSUser> users;
    private HashMap<Instant, EmergencyRecord> records;


    public EMSUser verifyUser(String username, String password) {

    }

    public void addEmergencyRecord(EmergencyRecord record) {

    }

    public EmergencyRecord getEmergencyRecord() {
        /* What are we supposed to get? An emergency record? But which one?*/
    }

    public EMSUser addUser(String firstname, String lastname, String username, String password) {

    }

    public EMSUser lookupUser(String username) {

    }

    public EmergencyRecord lookupEmergencyRecord(int instant) {

    }

    public boolean removeUser(EMSUser user) {
        /* Do we pass the user in argument, or just its username??*/
    }

    public EmergencyRecord[] getRecords() {

    }

    public EMSUser getUsers() {

    }

}
