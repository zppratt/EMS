package com.baconfiesta.ems.models.EmergencyRecord;

import com.baconfiesta.ems.models.EMSUser.EMSUser;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;



/**
 * Contains information about an emergency record
 * @author team_bacon_fiesta
 */
public class Metadata implements Serializable {

    private EMSUser createdBy;

    /**
     * The time the emergency record was created
     */
    private final Instant timeCreated;

    /**
     * The modifications done to the emergency record by time, user
     */
    private ArrayList<Instant> modifications;

    /**
     * Constructor to specify a time
     */
    protected Metadata(Instant time) {
        this.timeCreated = time;
    }

    /**
     *  Default constructor for a Metadata object
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
     * Retrieves the modifications to the record
     * @return the list of modifications by time, user
     */
    public ArrayList<Instant> getModifications() {
        if (modifications == null) {
            modifications = new ArrayList<>();
        }
        return modifications;
    }

    /**
     * Retrieves the time the record was created
     * @return the creation time
     */
    public Instant getTimeCreated() {
        return timeCreated;
    }

    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withZone(ZoneId.systemDefault());
        return dtf.format(getTimeCreated());
    }

    public void setCreatedBy(EMSUser creator) {
        this.createdBy = creator;
    }
}
