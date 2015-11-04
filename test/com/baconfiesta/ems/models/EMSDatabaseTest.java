package com.baconfiesta.ems.models;

import org.junit.Test;

import java.io.IOException;

public class EMSDatabaseTest {

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

}