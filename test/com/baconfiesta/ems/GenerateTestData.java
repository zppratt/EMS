package com.baconfiesta.ems;

import com.baconfiesta.ems.controller.EMSAdminController;
import com.baconfiesta.ems.models.EMSDatabase;
import com.baconfiesta.ems.models.EMSUser.EMSUser;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GenerateTestData {

    EMSAdminController ac;

    @Test
    public void testGenerateData() throws IOException, ClassNotFoundException, InterruptedException {

//        backupAndDeleteDatabase();

        ac = new EMSAdminController(new EMSUser("","","","",true),new EMSDatabase());
        ac.generateTestData();
    }

    private void backupAndDeleteDatabase() throws IOException {

        Path db = Paths.get("db/database.db");
        Path bk = Paths.get("db/database.db.bak");

        Files.deleteIfExists(bk);
        Files.copy(db, bk);

        System.err.println("db/database.db was backed up.");

        boolean result = Files.deleteIfExists(db);

        System.err.println( "db/database.db" + ( result ? " was deleted." : " was not deleted." ) );

    }

}