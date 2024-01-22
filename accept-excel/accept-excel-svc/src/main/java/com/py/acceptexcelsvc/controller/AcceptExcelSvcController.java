package com.py.acceptexcelsvc.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.py.acceptexcelsvc.service.ExcelProcessing;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/accept")
public class AcceptExcelSvcController {

	@Autowired
	private ExcelProcessing excelProcessing;
	
    @GetMapping("/{id}")
    public String getTestApi(String id){
        return "api working successfully";
    }
    
    @PostMapping("/file/")
    public String acceptFile(@RequestParam("file") MultipartFile file) {
    	String content=null,contentType=file.getContentType();
    	System.out.println("file type::: "+file.getContentType());
    	if(contentType.equals("text/plain")) {
	    	try {
	    			content =new String(file.getInputStream().readAllBytes());    			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("trying to extract contents from a file::" +e);
//				e.printStackTrace();
			}
	    	return "file Accepted"+file.getOriginalFilename()+" with content=> "+content;
    	}else if(contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
    		try {
				excelProcessing.processExcel(file.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("trying to process an excel:: "+e);
//				e.printStackTrace();
			}
    		return "excel reading success";
    	}
    	return "default Return";
    }
}
