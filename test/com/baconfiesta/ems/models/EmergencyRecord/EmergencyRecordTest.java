package com.baconfiesta.ems.models.EmergencyRecord;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Since this is pretty much a POJO, we only test method(s) out of the ordinary or as necessary.
 */
public class EmergencyRecordTest {

    EmergencyRecord record = EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord();

    @Test
    public void testModify() throws Exception {
        int oldSize = record.getMetadata().getModifications().size();
        record.modify();
        assertThat(record.getMetadata().getModifications().size(), is(greaterThan(oldSize)));
    }
}