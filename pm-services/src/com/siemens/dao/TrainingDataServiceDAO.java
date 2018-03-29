package com.siemens.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.siemens.pumpMonitoring.core.DbRowToObject;
import com.siemens.storage.DbConnection;
import com.siemens.storage.SelectOperations;

public class TrainingDataServiceDAO {

	public List<Object> get(String qStr, DbRowToObject obj) {
		List<Object> result = new ArrayList<>();
		Connection connection = null;
		try {
			connection = DbConnection.getDbConnection();
			SelectOperations opr = new SelectOperations();
			opr.select(connection, qStr, obj);
			result = obj.getData();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbConnection.releaseResources(connection);
		}
		return result;
	}
}
