package org.Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtils {
    private String filePath;
    private Workbook workbook;

    public ExcelUtils(String filePath) throws IOException {
        this.filePath = filePath;
        FileInputStream fis = new FileInputStream(filePath);
        this.workbook = new XSSFWorkbook(fis);
    }

    public List<List<String>> getSheetData(String sheetName) {
        List<List<String>> sheetData = new ArrayList<>();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return sheetData;
        for (Row row : sheet) {
            List<String> rowData = new ArrayList<>();
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
                rowData.add(cell.getStringCellValue());
            }
            sheetData.add(rowData);
        }
        return sheetData;
    }

    public List<String> getRowData(String sheetName, int rowIndex) {
        List<String> rowData = new ArrayList<>();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return rowData;
        Row row = sheet.getRow(rowIndex);
        if (row == null) return rowData;
        for (Cell cell : row) {
            cell.setCellType(CellType.STRING);
            rowData.add(cell.getStringCellValue());
        }
        return rowData;
    }

    public void close() throws IOException {
        if (workbook != null) {
            workbook.close();
        }
    }
}

