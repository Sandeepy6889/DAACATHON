package com.siemens.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DbRowToObject {

	void fill(ResultSet rs) throws SQLException;
	public List<Object> getData();
}
