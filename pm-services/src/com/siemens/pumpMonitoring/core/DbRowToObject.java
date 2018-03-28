package com.siemens.pumpMonitoring.core;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DbRowToObject {

	void fill(ResultSet rs) throws SQLException;
	
}
