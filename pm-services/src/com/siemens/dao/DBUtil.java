package com.siemens.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.siemens.pumpMonitoring.core.DbRowToObject;
import com.siemens.storage.DbConnection;
import com.siemens.storage.InsertOperations;
import com.siemens.storage.SelectOperations;
import com.siemens.storage.TableRow;

public class DBUtil {
	public static List<Object> get(String qStr, DbRowToObject obj) {
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

	public static List<Object> getColumnValues(String qString, String columnName) {
		List<Object> result = new ArrayList<>();
		Connection connection = null;
		try {
			connection = DbConnection.getDbConnection();
			SelectOperations opr = new SelectOperations();
			opr.select(connection, qString, result, columnName);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbConnection.releaseResources(connection);
		}
		return result;
	}

	public static boolean insert(TableRow row) {
		boolean isInserted = false;
		Connection connection = null;
		InsertOperations inOperation = new InsertOperations();
		try {
			connection = DbConnection.getDbConnection();
			inOperation.insert(connection, row);
			isInserted = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbConnection.releaseResources(connection);
		}
		return isInserted;
	}
}
