package com.baconfiesta.ems.models.EmergencyRecord;

import java.io.Serializable;

/**
 * An emergency record in the system
 * @author team_bacon_fiesta
 */
public class EmergencyRecord implements Serializable{

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
     * The route generated from the responder to the emergency
     */
    private Route route;

    /**
     * Default constructor for an emergency record
     * @param metadata the metadata
     * @param caller the caller
     * @param location the location
     * @param category the category
     * @param responder the responder
     * @param route the route
     */
    public EmergencyRecord(
            Metadata metadata,
            Caller caller,
            Location location,
            Category category,
            Responder responder,
            Route route) {
        this.setMetadata(metadata);
        this.setCaller(caller);
        this.setLocation(location);
        this.setCategory(category);
        this.setResponder(responder);
        this.setRoute(route);
    }

    /**
     * Constructor that builds a dummy object
     * @param dummy tells the Emergency record to build a dummy object
     */
    public EmergencyRecord(boolean dummy) {
        if (dummy) {
            this.setMetadata(new Metadata());
            this.setCaller(new Caller("John", "Smith", "2608675309"));
            this.setCategory(Category.CAR_CRASH);
            this.setLocation(new Location("999 Windmore Ave", "Indiana", 46825));
            this.setResponder(new Responder("2609999999", "999 Benchmark Ave", "Indiana", 46805));
            //this.setRoute(new Route());
        }
    }

    /**
     * Retrieve the metadata, including creation and modification information
     * @return the metadata
     */
    public Metadata getMetadata () {
        if (metadata==null) {
            metadata = new Metadata();
        }
        return metadata;
    }

    /**
     * Retrieves the caller for this emergency
     * @return the caller
     */
    public Caller getCaller() {
        return caller;
    }

    /**
     * Retrieves the location of the emergency
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Retrieves the category of the emergency
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Retrieves the responder to the emergency
     * @return the responder
     */
    public Responder getResponder() {
        return responder;
    }

    /**
     * Retrieves the route from the responder to the emergency
     * @return the route
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Sets the caller for the emergency
     * @param caller the caller
     */
    public void setCaller(Caller caller) {
        this.caller = caller;
    }

    /**
     * Sets the location of the emergency
     * @param location the location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Sets the category of the emergency
     * @param category the category
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Sets the responder for the emergency
     * @param responder the responder
     */
    public void setResponder(Responder responder) {
        this.responder = responder;
    }


    /**
     * Sets the route for the emergency
     * @param route the route
     */
    public void setRoute(Route route) {
        this.route = route;
    }

    /**
     * Sets the object containing the information about this record
     * @param metadata the metadata of this record
     */
    protected void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String toString() {
        return String.format("EmergencyRecord:%d", this.hashCode());
    }

}
