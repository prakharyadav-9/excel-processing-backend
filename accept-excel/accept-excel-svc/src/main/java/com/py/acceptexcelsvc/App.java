package com.py.acceptexcelsvc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.py.acceptexcelsvc.constants.Constants;

public class App {

	/*
	 * 
	 * HERE ALL EXPERIMENT will be done
	 */
	public static void main(String[] args) {
		String input="2Four";
		System.out.println("REgex using "+ Constants.NUM_REGEX);
		Pattern p = Constants.CompiledPatterns.get(Constants.NUM_REGEX);
		Matcher m = p.matcher(input);
		boolean matched = 
//				input.matches(Constants.NUM_REGEX);
			m.matches();
		System.out.printf("is matched %s \n",matched);

	}

}
