package com.baconfiesta.ems.models.EmergencyReport;

import ...
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * A report generator class in the system to create different kind of reports
 * */
public class Report {
    private final String recordTemplateFilename = "Template_Record.xls";
    private final String globalTemplateFilename = "Template_Stats.xls";

    public File generateRecordReport(EmergencyRecord emergencyRecord, String filename) {

        /* copying file from template */
        try {
            FileUtils.copyFile(recordTemplateFilename, filename)
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        /* Create new input stream from file */
        try {
            FileInputStream input = new FileInputStream(filename);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        /* Open excel file from input stream */
        try {
            POIFSFileSystem excelFile = new POIFSFileSystem(input);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

         /* Create workbook */
        try {
            HSSFWorkbook wb = new HSSFWorkbook(excelFile);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        /* MODIFICATION STARTS HERE */
        /* Get the sheet */
        Sheet sheet1 = wb.getSheet("Details");
        /* get a row */
        Row row = sheet1.getRow(0);
        /* get a cell */
        org.apache.poi.ss.usermodel.Cell cell = row.getCell(0);

        /* modify value of this cell */
        cell.setCellValue("Test1");

        /* MODIFICATION ENDS HERE */

         /* Create an output stream */
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

         /* Write the workbook in the output stream  */
        try {
            wb.write(out);
        } catch(Exception e) {
            e.printStackTrace();
        }

        /* Closing input and output */
        try {
            input.close();
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public File generateGlobalReport(EmergencyRecord[] emergencyRecords) {

    }

}