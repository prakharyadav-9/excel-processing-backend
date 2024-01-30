package com.py.acceptexcelsvc.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.http.ResponseEntity;

public final class Utils {


	    public static Response convert(ResponseEntity<?> responseEntity)throws Exception {
	        // Extract status code and body from ResponseEntity
	        int statusCode = responseEntity.getStatusCodeValue();
	        Object body = responseEntity.getBody();
	        // Create a JAX-RS Response using the status code and body
			ResponseBuilder responseBuilder = Response.status(statusCode).entity(body);

	        // Copy headers from ResponseEntity to Response
	        responseEntity.getHeaders().forEach((headerName, headerValues) ->
	                headerValues.forEach(headerValue -> responseBuilder.header(headerName, headerValue)));

	        // Build and return the JAX-RS Response
	        return responseBuilder.build();
	    }
	    
	    public static String grabCellFilterValue(Cell cell,String lableRegex, String contentRegex,Map<String,String> map) {
	    	Pattern lablePattern = Pattern.compile(lableRegex),
	    			contentPattern = Pattern.compile(contentRegex);
	    	CellType cellType = cell.getCellType();
	    	StringBuilder filterValue = new StringBuilder();
	    	switch(cellType) {
	    		default:
	    			System.err.printf("cannot grab the cell with regex:: %s",contentRegex);
	    			break;
	    		case STRING:
	    			Matcher lableMatcher =lablePattern.matcher(cell.getStringCellValue()),
	    				contentMatcher = contentPattern.matcher(cell.getStringCellValue());
	    			if(lableMatcher.find() && contentMatcher.find()) {
	    				map.put(lableMatcher.group(1), contentMatcher.group(1));
	    			}else {
	    				System.err.printf("could not find the corresponding lable with content");
	    			}
	    			
	    	}
	    	return filterValue.toString();
	    }
}
