package com.baconfiesta.ems.models.EmergencyRecord;

import com.baconfiesta.ems.models.EMSUser.EMSUser;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;

/**
 * Contains information about an emergency record
 * @author team_bacon_fiesta
 */
public class Metadata implements Serializable {

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
    private Category emergencyCategory;

    /**
     * Default constructor for a Metadata object
     */
    public Metadata() {
        this.timeCreated = Instant.now();
    }

    /**
     * Retrieves the user who created the record
     * @return the user
     */
    public EMSUser getCreatedBy() {
        return createdBy;
    }

    /**
     * Retrieves the user who modifies a record at an instant in time
     * @param instant the time the record was modified
     * @return the user who modified the record
     */
    public EMSUser getModifiedByTime(Instant instant) {
        return this.getModifications().get(instant);
    }

    /**
     * Retrieves the modifications to the record
     * @return the list of modifications by time, user
     */
    HashMap<Instant, EMSUser> getModifications() {
        /* Should we return a copy or itself? */
        return modifications;
    }

    /**
     * Retrieves the time the record was created
     * @return the creation time
     */
    public Instant getTimeCreated() {
        return timeCreated;
    }

    /**
     * Receives the category of the emergency
     * @return the category
     */
    public Category getEmergencyCategory() {
        return emergencyCategory;
    }

    /**
     * Sets the category of the emergency
     * @param emergencyCategory the category
     */
    public void setEmergencyCategory(Category emergencyCategory) {
        this.emergencyCategory = emergencyCategory;
    }
}
