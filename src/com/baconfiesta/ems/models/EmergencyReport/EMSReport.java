package com.baconfiesta.ems.models.EmergencyReport;

import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * A report generator class in the system to create different kind of reports
 * */
public class Report {
    private final String recordTemplateFilename = "template_Record.xls";
    private final String globalTemplateFilename = "template_Stats.xls";

    public File generateRecordReport(EmergencyRecord emergencyRecord, String filename) {

        /* copying file from template */
        try {
            FileUtils.copyFile(new File(recordTemplateFilename), new File(filename));
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        /* Create new input stream from file */
        FileInputStream input;
        try {
            input = new FileInputStream(filename);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        /* Open excel file from input stream */
        POIFSFileSystem excelFile;
        try {
            excelFile = new POIFSFileSystem(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

         /* Create workbook */
        HSSFWorkbook wb;
        try {
            wb = new HSSFWorkbook(excelFile);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        /* MODIFICATION STARTS HERE */
        /* Get the sheet */
        Sheet sheet1 = wb.getSheet("Details");

        Row row;
        org.apache.poi.ss.usermodel.Cell cell;

        /* Caller first name */
        row = sheet1.getRow(3);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getCaller().getFirstName());

        /* Caller lastname */
        row = sheet1.getRow(4);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getCaller().getLastName());

        /* Caller Phone number */
        row = sheet1.getRow(5);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getCaller().getPhone());

        /* Emergency Address */
        row = sheet1.getRow(7);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getLocation().getAddress());

        /* Emergency Address */
        row = sheet1.getRow(8);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getLocation().getZip());

        /* Emergency State */
        row = sheet1.getRow(9);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getLocation().getState());

        /* Emergency Category */
        row = sheet1.getRow(9);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getCategory().toString()); // NEEDS TO BE TRANSFORMED IN STRING

        /* Emergency Response Time */
        row = sheet1.getRow(10);
        cell = row.getCell(2);
        //cell.setCellValue(emergencyRecord); // DON'T KNOW WHERE THE RESPONSE TIME IS LOCATED

        /* Responder Address */
        row = sheet1.getRow(12);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getResponder().getAddress());

        /* Responder ZIP */
        row = sheet1.getRow(13);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getResponder().getZip());

        /* Responder State */
        row = sheet1.getRow(14);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getResponder().getState());

        /* Route Chosen */
        row = sheet1.getRow(16);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getRoute().getAlternateRouteSelectedString());

        /* Emergency Case Created By */
        row = sheet1.getRow(18);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getMetadata().getCreatedBy().toString()); // NEEDS TO BE TRANSFORMED TO STRING

         /* Emergency Case Time Created */
        row = sheet1.getRow(18);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getMetadata().getTimeCreated().toString()) // NEEDS TO BE TRANSFORMED TO STRING

         /* Emergency Case Number of Modifications */
        row = sheet1.getRow(18);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getMetadata().getModifications().size())


        /* MODIFICATION ENDS HERE */

         /* Create an output stream */
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
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
        return null;
    }

    public File generateGlobalReport(EmergencyRecord[] emergencyRecords) {
        return null;
    }

}