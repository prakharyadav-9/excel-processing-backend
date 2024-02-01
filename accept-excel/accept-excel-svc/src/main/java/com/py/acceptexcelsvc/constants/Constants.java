package com.py.acceptexcelsvc.constants;

import java.util.Map;
import java.util.regex.Pattern;

public final class Constants {

	public final static String DataTypeRegex = "\\[([^\\]]+)\\]";
	public final static String HeaderLableRegex ="^(.*?)\\[.*?\\]$";
	public final static String NUM_REGEX = "[0-9]+";
	public final static String STR_REGEX = "^[a-zA-Z\\s]*$";
	public final static Map<String,String> ColTypeRegex = Map.ofEntries(
															Map.entry("NUMERIC", NUM_REGEX),
															Map.entry("STRING", STR_REGEX)
															);
	public final static Map<String,Pattern> CompiledPatterns = Map.ofEntries(
																			Map.entry(NUM_REGEX, Pattern.compile(NUM_REGEX)),
																			Map.entry(STR_REGEX, Pattern.compile(STR_REGEX))
																			);
	
}
