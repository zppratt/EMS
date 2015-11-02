package com.baconfiesta.ems.models.EmergencyRecord;

import com.baconfiesta.ems.models.EMSUser;

import java.time.Instant;
import java.util.HashMap;

public class Metadata {

    private EMSUser createdBy;
    private HashMap<Instant, EMSUser> modifications;

    public EMSUser getCreatedBy() {
        return createdBy;
    }

    public EMSUser getModifiedByTime(Instant instant) {

    }

    public HashMap<Instant, EMSUser> getModifications() {
        /* Should we return a copy or itself? */
        return modifications;
    }
}
