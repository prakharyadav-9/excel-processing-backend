package com.py.acceptexcelsvc.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.regex.Matcher;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.io.CsvListWriter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

import com.py.acceptexcelsvc.constants.Constants;
import com.py.acceptexcelsvc.entity.ParsedCSVRows;

import com.py.acceptexcelsvc.util.Utils;

@Service
//@SingleTon
public class ExcelProcessing {
	
	private static final Logger logger = LogManager.getLogger(ExcelProcessing.class);

	
	public Response processExcel(File file){
		return null;	
	}
	
	private byte[] convertWorkbookToCsv(Workbook workbook) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CsvListWriter csvWriter = new CsvListWriter(writer, CsvPreference.STANDARD_PREFERENCE)) {

            // Iterate through the workbook and write CSV data
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
                    Row row = sheet.getRow(j);
                    List<String> rowData = new ArrayList<>();

                    for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {
                        Cell cell = row.getCell(k);
                        rowData.add(getCellValueAsString(cell));
                    }

                    // Write the row to the CSV
                    csvWriter.write(rowData);
                }
            }

            // Flush and close the CsvListWriter
            csvWriter.flush();
            csvWriter.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return new byte[0];
        }
    }

    private String getCellValueAsString(Cell cell) {
        // Implement your logic to convert cell value to string
        return cell.toString();
    }
	
	public ResponseEntity<byte[]> processExcel(InputStream file) {
		logger.info("Processing the excel file:",file);
		ParsedCSVRows parsedCSVRows=new ParsedCSVRows();
		ArrayList<String> headersTypes=new ArrayList<>();
		try(Workbook wb = new XSSFWorkbook(file);){
			prepareHeader(wb,headersTypes); // prepare all the headers types 
			parseExcelDataRows(wb,headersTypes,parsedCSVRows);
			logger.info("CSV parsed successfully with unaffected rows {} & affected {}",parsedCSVRows.getUnaffectedCount(),parsedCSVRows.getAffectedCount());
			Workbook affectedRowsWorkbook=prepareUnparsedCSV(parsedCSVRows.getAffectedRows());
			byte[] csvBytes = convertWorkbookToCsv(affectedRowsWorkbook);
			HttpHeaders headers = new HttpHeaders();
//	        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);;
	        headers.setContentDispositionFormData("attachment", "data.csv");
			return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
			
		} catch (EncryptedDocumentException e) {
			System.err.println("cannot process the document "+e);
//			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException occured for parsing the file "+e);
//			e.printStackTrace();
		}
		return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
	}
	
	private Workbook prepareUnparsedCSV(ArrayList<Row> affectedRows) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		// Iterate through the ArrayList and write rows to the Sheet
        for (int i = 0; i < affectedRows.size(); i++) {
            Row row = affectedRows.get(i);
            Row newRow = sheet.createRow(i);

            // Iterate through the cells in the row and copy them to the new row
            for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                Cell cell = row.getCell(j);
                Cell newCell = newRow.createCell(j);

                // Copy the cell value and style
                if (cell != null) {
                	CellType cellType = cell.getCellType(); 
                	switch (cellType) {
                    case STRING:
                        newCell.setCellValue( cell.getStringCellValue());
                    break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            newCell.setCellValue(cell.getDateCellValue());
                        } else {
                            newCell.setCellValue(cell.getNumericCellValue());
                        }
                    break;
                    case BOOLEAN:
                        newCell.setCellValue(cell.getBooleanCellValue());
                    break;
                    case FORMULA:
                        newCell.setCellValue(cell.getCellFormula());
                    break;
                    default:
                    	logger.error("COuldnot set the cell value of type {} to new Cell",cellType);
                }
                	copyCellStyle(cell.getCellStyle(), newCell, workbook);
                }
            }
        }
        logger.debug("unparsable records data successfully prepared of length {}",affectedRows.size());
        return workbook;
	}
	
	private static void copyCellStyle(CellStyle sourceStyle, Cell targetCell, Workbook targetWorkbook) {
        // Create a new CellStyle in the target workbook
        CellStyle targetStyle = targetWorkbook.createCellStyle();

        // Copy relevant style properties
        targetStyle.setAlignment(sourceStyle.getAlignment());
        targetStyle.setVerticalAlignment(sourceStyle.getVerticalAlignment());
        targetStyle.setBorderBottom(sourceStyle.getBorderBottom());
        targetStyle.setBorderTop(sourceStyle.getBorderTop());
        targetStyle.setBorderLeft(sourceStyle.getBorderLeft());
        targetStyle.setBorderRight(sourceStyle.getBorderRight());
        targetStyle.setBottomBorderColor(sourceStyle.getBottomBorderColor());
        targetStyle.setTopBorderColor(sourceStyle.getTopBorderColor());
        targetStyle.setLeftBorderColor(sourceStyle.getLeftBorderColor());
        targetStyle.setRightBorderColor(sourceStyle.getRightBorderColor());
        targetStyle.setFillForegroundColor(sourceStyle.getFillForegroundColor());
        targetStyle.setFillPattern(sourceStyle.getFillPattern());

        // Apply the new style to the target cell
        targetCell.setCellStyle(targetStyle);
    }
	
	
	private void parseExcelDataRows(Workbook wb, ArrayList<String> headersTypes, ParsedCSVRows parsedCSVRows) {
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
//					System.out.println("should have been "+cellTy+" but got "+cellType.toString());
					switch(cellType) {
					default:
						logger.error("could not determine cell types from the know type of {} row and {} column in {} sheet",item,cellColIdx,sheet);
					break;
					
					case NUMERIC:
						if(cellTy.equalsIgnoreCase(cellType.toString())) {
							// processing only numeric
//							System.out.println("dealing with numeric ");
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
		parsedCSVRows.setAffectedRows(affectedRows);
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

}
