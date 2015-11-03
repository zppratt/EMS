package com.baconfiesta.ems.models.EmergencyRecord;

import com.baconfiesta.ems.models.EMSUser;

import java.time.Instant;
import java.util.HashMap;

/**
 * Contains information about an emergency record
 * @author baconfiesta
 */
public class Metadata {

    /**
     * The user who created the emergency record
     */
    private EMSUser createdBy;

    /**
     * The time the emergency record was created
     */
    private Instant timeCreated;

    /**
     * The modifications done to the emergency record by time, user
     */
    private HashMap<Instant, EMSUser> modifications;

    /**
     * The category of the emergency
     */
    private String emergencyCategory;

    /**
     * Default constructor for a Metadata object
     */
    protected Metadata() {
        this.timeCreated = Instant.now();
    }

    public EMSUser getCreatedBy() {
        return createdBy;
    }

    public EMSUser getModifiedByTime(Instant instant) {

    }

    public HashMap<Instant, EMSUser> getModifications() {
        /* Should we return a copy or itself? */
        return modifications;
    }

    public Instant getTimeCreated() {
        return timeCreated;
    }

    public String getEmergencyCategory() {
        return emergencyCategory;
    }

    public void setEmergencyCategory(String emergencyCategory) {
        this.emergencyCategory = emergencyCategory;
    }
}
