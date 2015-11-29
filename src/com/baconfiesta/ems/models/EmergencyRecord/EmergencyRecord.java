package com.baconfiesta.ems.models.EmergencyRecord;

import com.baconfiesta.ems.models.EMSUser.EMSUser;

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
     * The description of the emergency
     */
    private String description;

    /**
     * Default constructor for an emergency record
     * @param metadata the metadata
     * @param caller the caller
     * @param location the location
     * @param category the category
     * @param responder the responder
     * @param route the route
     * @param creator the creator of the record
     */
    public EmergencyRecord(
            Metadata metadata,
            Caller caller,
            Location location,
            Category category,
            Responder responder,
            Route route,
            String description,
            EMSUser creator
    ) {
        this.setMetadata(metadata);
        this.metadata.setCreatedBy(creator);
        this.setCaller(caller);
        this.setLocation(location);
        this.setCategory(category);
        this.setResponder(responder);
        this.setRoute(route);
        this.setDescription(description);
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
     * Retrieves the description of the emergency
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the emergency record in paragraph form
     * @return the paragraph form
     */
    public String getParagraphForm(){
        String paragraph = "";

        paragraph += "Emergency Time: " + getMetadata().toString() + "\n";
        paragraph += "Category: " + category.name() + "\n";

        paragraph += "\nCaller Information:\n";
        paragraph += "Name: " + caller.getFirstName() + " " + caller.getLastName() + "\n";
        paragraph += "Phone: " + caller.getPhone() + "\n";
        paragraph += "Address: " + location.getAddress() + "\n";
        paragraph += "City: " + location.getCity() + "\n";
        paragraph += "State: " + location.getState() + "\n";

        paragraph += "\nDescription\n";
        paragraph += description + "\n";

        paragraph += "\nResponder Information:\n";
        paragraph += "Phone: " + responder.getPhoneNumber() + "\n";
        paragraph += "Address: " + responder.getAddress() + "\n";
        paragraph += "State: " + responder.getState() + "\n";
        paragraph += "City: " + responder.getCity() + "\n";

        return paragraph;

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

    /**
     * Sets the description of the emergency
     * @param description of this record
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return String.format("%s\n : %s\n", getMetadata(), getMetadata().getCreatedBy());
    }

}
