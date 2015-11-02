package com.baconfiesta.ems.models.EmergencyRecord;

import java.time.Instant;

public class EmergencyRecord {

    private int id; // Didn't we say that we would use instants instead of ids???
    private Metadata metadata;
    private Caller caller;
    private Location location;
    private Category category;
    private Responder responder;
    private Instant timeCreated;
    private Route route;

    public int getId() {
        return id;
    }

    public Metadata getMetadata () {
        return metadata;
    }

    public Caller getCaller() {
        return caller;
    }

    public Location getLocation() {
        return location;
    }

    public Category getCategory() {
        return category;
    }

    public Responder getResponder() {
        return responder;
    }

    public Route getRoute() {
        return route;
    }

    public void setCaller(Caller caller) {
        this.caller = caller;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setResponder(Responder responder) {
        this.responder = responder;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
