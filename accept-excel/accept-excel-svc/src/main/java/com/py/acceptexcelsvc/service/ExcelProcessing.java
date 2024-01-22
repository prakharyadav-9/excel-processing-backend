package com.py.acceptexcelsvc.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.catalina.connector.Response;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelProcessing {
	
	public Response processExcel(File file){
		return null;	
	}
	
	public Response processExcel(InputStream file) {
		System.out.println("read the file in inputStream "+file);
//		System.out.println("is Empty file :: "+file.);
		try(Workbook wb = new XSSFWorkbook(file);){
			readExcel(wb); // printing the input workSheet into console
			
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			System.err.println("cannot process the document "+e);
//			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("IOException occured for parsing the file "+e);
//			e.printStackTrace();
		}
		return null;
	}
	
	private void readExcel(Workbook workbook) {
		// Iterate through all sheets
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);

            // Iterate through all rows in the sheet
            for (Row row : sheet) {
                // Iterate through all cells in the row
                for (Cell cell : row) {
                    // Get the value from the cell
                    CellType cellType = cell.getCellType();
                    switch (cellType) {
                        case STRING:
                            System.out.print(cell.getStringCellValue() + "\t");
                            break;
                        case NUMERIC:
                            System.out.print(cell.getNumericCellValue() + "\t");
                            break;
                        case BOOLEAN:
                            System.out.print(cell.getBooleanCellValue() + "\t");
                            break;
                        case BLANK:
                            System.out.print("[BLANK]\t");
                            break;
                        // Handle other cell types as needed
                        default:
                            System.out.print("[UNKNOWN]\t");
                    }
                }
                System.out.println(); // Move to the next line after each row
            }
        }
	}
}
