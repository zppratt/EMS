package com.baconfiesta.ems.models;

import com.baconfiesta.ems.models.EMSDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;

import static org.powermock.api.easymock.PowerMock.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {EMSDatabase.class} )
public class EMSDatabaseTest {

    /**
     * Class under test, sometimes mocked, sometimes a real object
     */
    EMSDatabase database;

    /**
     * Test the opening and closing of a database
     */
    @Test
    public void testDatabaseOpenAndClose() {
        EMSDatabase db = new EMSDatabase();
        try {
            db.closeDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the creation of a database if no db file exists
     */
    @Test
    public void testDatabaseNoDirectory() throws Exception {
        EMSDatabase db = PowerMock.createMock(EMSDatabase.class);
        File fileMock = PowerMock.createMock(File.class);
        expectNew(File.class, "./db").andReturn(fileMock);
//        EasyMock.expect();
        replayAll();
        try {
            db.closeDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        verifyAll();
    }

    /**
     * Test the creation of a database if no db file exists
     */
    @Test
    public void testDatabaseNoFile() {
        EMSDatabase db = new EMSDatabase();
        try {
            db.closeDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}