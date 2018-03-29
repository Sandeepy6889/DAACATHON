package com.siemens.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TableRow {

	private String tablename;
	Map<String, Object> columns;

	public TableRow(String tablename) {
		this.tablename = tablename;
		columns = new HashMap<>();
	}

	public void set(String columnName, Object columnValue) {
		columns.put(columnName.trim(), columnValue);
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public Object getValue(String columnName) {
		return columns.get(columnName);
	}

	public List<String> getColumnNames() {
		List<String> clmns = new ArrayList<>();
		for (Entry<String, Object> entry : columns.entrySet())
			clmns.add((String) entry.getKey());
		return clmns;
	}

	public void set(String... columns) {
		for (int i = 0; i < columns.length; i++) {
			set(columns[i], "");
		}
	}
}
