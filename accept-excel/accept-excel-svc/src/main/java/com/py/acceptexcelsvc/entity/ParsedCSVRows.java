package com.py.acceptexcelsvc.entity;

import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Row;

public final class ParsedCSVRows {
	private ArrayList<Row> affectedRows;
	private ArrayList<Row> unaffectedRows;

	public ArrayList<Row> getAffectedRows() {
		return affectedRows;
	}
	public void setAffectedRows(ArrayList<Row> affectedRows) {
		this.affectedRows = affectedRows;
	}
	
	public ArrayList<Row> getUnaffectedRows() {
		return unaffectedRows;
	}
	public void setUnaffectedRows(ArrayList<Row> unaffectedRows) {
		this.unaffectedRows = unaffectedRows;
	}
	public int getAffectedCount() {
		return null!=affectedRows?affectedRows.size():0;
	}
	public int getUnaffectedCount() {
		return null!=unaffectedRows?unaffectedRows.size():0;
	}
}
