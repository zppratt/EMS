package com.baconfiesta.ems.models.EmergencyRecord;

import java.io.Serializable;
import java.time.Instant;

/**
 * An emergency record in the system
 * @author team_bacon_fiesta
 */
public class EmergencyRecordBuilder implements Serializable {

    /**
     * Info about the record
     */
    private Metadata metadata;

    /**
     * The caller who initiated the emergency in the system
     */
    private Caller caller;

    /**
     * The location of the emergency
     */
    private Location location;

    /**
     * The type of emergency
     */
    private Category category;

    /**
     * Information about the responder(s) to the emergency
     */
    private Responder responder;

    /**
     * The description of the emergency
     */
    private String description;

    /**
     * The route generated from the responder to the emergency
     */
    private Route route;

    public EmergencyRecordBuilder() {
        metadata = new Metadata();
        caller = new Caller("","","");
        location = new Location("","", "");
        category = Category.HOAX;
        responder = new Responder("","","", "");
        description = "";
        //route = new Route();
    }

    public EmergencyRecordBuilder withMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public EmergencyRecordBuilder withTime(Instant time) {
        this.metadata = new Metadata(time);
        return this;
    }

    public EmergencyRecordBuilder withCaller(Caller caller) {
        this.caller = caller;
        return this;
    }

    public EmergencyRecordBuilder withLocation(Location location) {
        this.location = location;
        return this;
    }

    public EmergencyRecordBuilder withCategory(Category category) {
        this.category = category;
        return this;
    }

    public EmergencyRecordBuilder withResponder(Responder responder) {
        this.responder = responder;
        return this;
    }

    public EmergencyRecordBuilder withDescription(String description) {
        this.description = description;
        return this;
    }


    protected void setDescription(String description) {
        this.description = description;
    }

    public static EmergencyRecordBuilder newBuilder() {
        return new EmergencyRecordBuilder();
    }

    public EmergencyRecord getNewEmergencyRecord() {
        return new EmergencyRecord(metadata, caller, location, category, responder, route, description); // with all the parameters
    }

}
