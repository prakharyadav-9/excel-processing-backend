package com.py.acceptexcelsvc.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.catalina.connector.Response;
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
		Workbook workbook = WorkbookFactory.create(file);
		workbook.close();
		return null;
	}
}
