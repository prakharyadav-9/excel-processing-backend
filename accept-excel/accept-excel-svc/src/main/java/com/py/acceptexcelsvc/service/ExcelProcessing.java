package com.py.acceptexcelsvc.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.py.acceptexcelsvc.constants.Constants;

import com.py.acceptexcelsvc.util.Utils;

@Service
//@SingleTon
public class ExcelProcessing {
	
	private static final Logger logger = LogManager.getLogger(ExcelProcessing.class);

	
	public Response processExcel(File file){
		return null;	
	}
	
	public javax.ws.rs.core.Response processExcel(InputStream file) {
		logger.info("Processing the excel file:",file);
//		System.out.println("read the file in inputStream "+file);
//		System.out.println("is Empty file :: "+file.);
		ArrayList<String> headersTypes=new ArrayList<>();
		ArrayList<Row> afftecedRows = new ArrayList<>();
		Response response = Response.status(Status.BAD_REQUEST).build();
		try(Workbook wb = new XSSFWorkbook(file);){
			prepareHeader(wb,headersTypes); // prepare all the headers types 
			afftecedRows= parseExcelDataRows(wb,headersTypes);
			response = Response.status(Status.OK).build();
			
		} catch (EncryptedDocumentException e) {
			System.err.println("cannot process the document "+e);
//			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException occured for parsing the file "+e);
//			e.printStackTrace();
		}
		return response;
	}
	
	private ArrayList<Row> parseExcelDataRows(Workbook wb, ArrayList<String> headersTypes) {
		ArrayList<Row> affectedRows=new ArrayList<>();
		for(Sheet sheet:wb) {
			int item=-1;
			for(Row row:sheet) {
				boolean isAfftected = false;
				item++;
				if(0==item) {
					continue; // leaving the header row
				}
				int cellColIdx=0;
				for(Cell cell:row) {
					String cellTy = headersTypes.get(cellColIdx);
					String regex = Constants.ColTypeRegex.get(cellTy);
					CellType cellType = cell.getCellType();
					System.out.println("should have been "+cellTy+" but got "+cellType.toString());
					switch(cellType) {
					default:
						logger.error("could not determine cell types from the know type of {} row and {} column in {} sheet",item,cellColIdx,sheet);
					break;
					
					case NUMERIC:
						if(cellTy.equalsIgnoreCase(cellType.toString())) {
							// processing only numeric
							System.out.println("dealing with numeric ");
						}else {
							// failed numeric parse
//							need to add to affected row
							affectedRows.add(row);
							isAfftected=true;
						}
					break;
					
					case STRING:
						String cellValue = cell.getStringCellValue().trim();
						Matcher matched = Constants.CompiledPatterns.get(regex).matcher(cellValue);
						if(!matched.matches()) {
							affectedRows.add(row);
							isAfftected=true;
						}else{
							// for successfull string match
						}
					break;
					
					case BLANK:
						isAfftected=true;
						affectedRows.add(row);
					break;
					}
					if(isAfftected) {
						// breaking the row checking
						break;
					}
					cellColIdx++; // incrementing to next column
				}
			}
		}
		logger.info("parsing complete with {} affected rows",affectedRows.size());
		return affectedRows;
	}

	private void prepareHeader(Workbook wb,ArrayList<String> headersType) {
		int numerOfSheets = wb.getNumberOfSheets();
		for(int i=0;i<numerOfSheets;i++) {
			Sheet sheet = wb.getSheetAt(i);
			Row headerRow = sheet.getRow(0); // passing only the header to get datatypes
			Utils.grabCellFilterValue(headerRow, Constants.HeaderLableRegex, Constants.DataTypeRegex, headersType);
		}
		System.out.println(headersType);
	}

	private void readExcelToConsole(Workbook workbook) {
		logger.info("reading the workbook");
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
