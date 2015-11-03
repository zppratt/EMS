package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;

import java.time.Instant;
import java.util.HashMap;

public class EMSUser {

    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private HashMap<Instant, EmergencyRecord> records;
    private Boolean admin;

    public EMSUser(String firstname, String lastname, String username, String password, Boolean admin) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.admin = admin;

    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUsername() {
        return username;
    }

    public Boolean checkPassword(String password) {
        return this.getPassword().equals(password);
    }

    public HashMap<Instant, EmergencyRecord> getRecords() {

    }

    public Boolean checkAdmin() {

    }

    public void setAdmin() {

    }

    private String getPassword() {
        return password;
    }
}
