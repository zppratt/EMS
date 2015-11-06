package com.baconfiesta.ems.models.EmergencyRecord;

/**
 * A route in the system from the responder to the emergency
 */
public class Route {

    /**
     * The
     */
    private double source;
    private double destination;

    public void determineNearestResponder(EmergencyRecord record) {

        /* Determine emergency responder according to the type of the emergency:*/
        switch (record.getCategory()) {
            /* Fire Department*/
            case FIRE:
                break;
            /* Police Department */
            case CRIME:
                break;
            case CAR_CRASH:
                break;
            /* Health Department */
            case MEDICAL:
                break;
            default:
                break;
        }
    }

}