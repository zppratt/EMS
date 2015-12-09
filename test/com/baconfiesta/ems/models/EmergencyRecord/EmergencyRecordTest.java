package com.baconfiesta.ems.models.EmergencyRecord;

import com.baconfiesta.ems.models.EMSUser.EMSUser;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Since this is pretty much a POJO, we only test method(s) out of the ordinary or as necessary.
 */
public class EmergencyRecordTest {

    EMSUser user = new EMSUser("","","","",true);
    EmergencyRecord record = EmergencyRecordBuilder.newBuilder().getNewEmergencyRecord(user);

    @Test
    public void testModify() throws Exception {
        int oldSize = record.getMetadata().getModifications().size();
        record.modify();
        assertThat(record.getMetadata().getModifications().size(), is(greaterThan(oldSize)));
    }
}