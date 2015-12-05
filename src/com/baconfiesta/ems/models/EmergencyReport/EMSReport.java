package com.baconfiesta.ems.models.EmergencyReport;

import com.baconfiesta.ems.models.EmergencyRecord.EmergencyRecord;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * A report generator class in the system to create different kind of reports
 * */
public class EMSReport {

    /**
     * The name and path of the template for a record report generation
     * */
    private static final String recordTemplateFilename = "templates/template_record.xls";

    /**
     * The name and path of the template for a stats report generation
     * */
    private static final String statsTemplateFilename = "templates/template_stats.xls";


    /**
     * \brief Generates a detailed report for a unique emergency record
     * @param emergencyRecord the emergency record to generate a report for
     * @param filename the name of the file (and its path) where we want to store the report */
    public static File generateRecordReport(EmergencyRecord emergencyRecord, String filename) {

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

       /* Emergency Case ID */
        row = sheet1.getRow(0);
        cell = row.getCell(1);
        cell.setCellValue(emergencyRecord.getMetadata().getTimeCreated().toEpochMilli());

        /* Caller first name */
        row = sheet1.getRow(3);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getCaller().getFirstName());

        /* Caller last name */
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
        cell.setCellValue(emergencyRecord.getLocation().getCity());

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
       // cell.setCellValue(emergencyRecord.getRoute().getRouteDuration());

        /* Responder Phone Number */
        row = sheet1.getRow(12);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getResponder().getPhoneNumber());

        /* Responder Address */
        row = sheet1.getRow(13);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getResponder().getAddress());

        /* Responder City */
        row = sheet1.getRow(14);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getResponder().getCity());

        /* Responder State */
        row = sheet1.getRow(15);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getResponder().getState());

        /* Route Chosen */
        row = sheet1.getRow(17);
        cell = row.getCell(2);
        //cell.setCellValue(emergencyRecord.getRoute().getAlternateRouteSelectedString());

        /* Emergency Case Created By */
        row = sheet1.getRow(19);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getMetadata().getCreatedBy().toString()); // NEEDS TO BE TRANSFORMED TO STRING

         /* Emergency Case Time Created */
        row = sheet1.getRow(20);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getMetadata().toString());

         /* Emergency Case Number of Modifications */
        row = sheet1.getRow(21);
        cell = row.getCell(2);
        cell.setCellValue(emergencyRecord.getMetadata().getModifications().size());


        /* MODIFICATION ENDS HERE */

         /* Create an output stream */
        FileOutputStream out;
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

        return new File(filename);
    }


    /**
     * \brief Generates a detailed report and statistics for several emergency records
     * @param emergencyRecords the array of emergency records to generate a report for
     * @param filename the name of the file (and its path) where we want to store the report
     * */
    public static void generateStatsReport(EmergencyRecord[] emergencyRecords, String filename) {

        int emergencyRecordsLength = emergencyRecords.length;
        int i;

        if(emergencyRecordsLength > 2000) {
            System.err.println("Number of records too large (" + emergencyRecordsLength + "): record generation aborted");
            return;
        }

        /* copying file from template */
        try {
            FileUtils.copyFile(new File(statsTemplateFilename), new File(filename));
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        /* Create new input stream from file */
        FileInputStream input;
        try {
            input = new FileInputStream(filename);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        /* Open excel file from input stream */
        POIFSFileSystem excelFile;
        try {
            excelFile = new POIFSFileSystem(input);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

         /* Create workbook */
        HSSFWorkbook wb;
        try {
            wb = new HSSFWorkbook(excelFile);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        /* MODIFICATION STARTS HERE */
        /* Get the sheet */
        Sheet sheet1 = wb.getSheet("Details");

        Row row;
        org.apache.poi.ss.usermodel.Cell cell;


        /* For each emergency record, entering information in the excel file */
        for(i=0; i<emergencyRecordsLength; i++) {
            row = sheet1.getRow(i+1);

            /* Emergency ID */
            cell = row.getCell(0);
            cell.setCellValue(emergencyRecords[i].getMetadata().getTimeCreated().toEpochMilli());

            /* Creation Time */
            cell = row.getCell(1);
            cell.setCellValue(emergencyRecords[i].getMetadata().toString());

            /* Caller's first name */
            cell = row.getCell(2);
            cell.setCellValue(emergencyRecords[i].getCaller().getFirstName());

            /* Caller's last name */
            cell = row.getCell(3);
            cell.setCellValue(emergencyRecords[i].getCaller().getLastName());

            /* Caller's Phone Number */
            cell = row.getCell(4);
            cell.setCellValue(emergencyRecords[i].getCaller().getPhone());

            /* Emergency Address */
            cell = row.getCell(5);
            cell.setCellValue(emergencyRecords[i].getLocation().getAddress());

            /* Emergency City */
            cell = row.getCell(6);
            cell.setCellValue(emergencyRecords[i].getLocation().getCity());

            /* Emergency State */
            cell = row.getCell(7);
            cell.setCellValue(emergencyRecords[i].getLocation().getState());

            /* Emergency Category */
            cell = row.getCell(8);
            cell.setCellValue(emergencyRecords[i].getCategory().toString());

            /* Emergency Response Time */
            cell = row.getCell(9);
            //cell.setCellValue(emergencyRecords[i].getRoute().getRouteDuration());

            /* Responder's Address */
            cell = row.getCell(10);
            cell.setCellValue(emergencyRecords[i].getResponder().getAddress());

            /* Responder's City */
            cell = row.getCell(11);
            cell.setCellValue(emergencyRecords[i].getResponder().getCity());

            /* Responder's State */
            cell = row.getCell(12);
            cell.setCellValue(emergencyRecords[i].getResponder().getState());

            /* Responder's Phone Number */
            cell = row.getCell(13);
            cell.setCellValue(emergencyRecords[i].getResponder().getPhoneNumber());

            /* Route Chosen */
            cell = row.getCell(14);
            //cell.setCellValue(emergencyRecords[i].getRoute().getAlternateRouteSelectedString());

            /* Number of Modifications */
            cell = row.getCell(15);
            //cell.setCellValue(emergencyRecords[i].getMetadata().getModifications().size());
        }

        /* Writing starting and ending date period */
        sheet1 = wb.getSheet("Summary Report");
        row = sheet1.getRow(0);
        cell = row.getCell(1);

        if(emergencyRecordsLength > 0) {
            cell.setCellValue(emergencyRecords[emergencyRecordsLength-1].getMetadata().toString());
            cell = row.getCell(27);
            cell.setCellValue(emergencyRecords[0].getMetadata().toString());
        }
        else
            cell.setCellValue("No Emergency Record to Display");


         /* MODIFICATION ENDS HERE */

         /* Create an output stream */

        FileOutputStream out;
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

}