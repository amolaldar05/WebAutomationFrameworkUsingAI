package org.Utils;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ExcelUtil {

    /**
     * Reads data from the given Excel file and sheet name.
     * Returns the data as a list of maps where each map represents a row with column headers as keys.
     */
    public static List<Map<String, String>> readDataFromExcel(String filePath, String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet \"" + sheetName + "\" not found in " + filePath);
            }

            Iterator<Row> rowIterator = sheet.iterator();
            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();

            // Read headers
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            // Read data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, String> rowData = new LinkedHashMap<>();
                int cellIndex = 0;

                for (Cell cell : row) {
                    cell.setCellType(CellType.STRING); // Force all data as String
                    rowData.put(headers.get(cellIndex), cell.getStringCellValue());
                    cellIndex++;
                }
                dataList.add(rowData);
            }

        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
        }

        return dataList;
    }

    /**
     * Writes data to the given Excel file and sheet name.
     * Accepts data as a list of maps where keys are column headers.
     */
    public static void writeDataToExcel(String filePath, String sheetName, List<Map<String, String>> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            if (data.isEmpty()) {
                throw new IllegalArgumentException("No data provided to write.");
            }

            // Create header row
            Row headerRow = sheet.createRow(0);
            Set<String> headers = data.get(0).keySet();
            int headerCol = 0;

            for (String header : headers) {
                Cell cell = headerRow.createCell(headerCol++);
                cell.setCellValue(header);
            }

            // Create data rows
            int rowNum = 1;
            for (Map<String, String> rowMap : data) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (String header : headers) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(rowMap.getOrDefault(header, ""));
                }
            }

            // Write to file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("Data written successfully to: " + filePath);

        } catch (IOException e) {
            System.err.println("Error writing Excel file: " + e.getMessage());
        }
    }
}
