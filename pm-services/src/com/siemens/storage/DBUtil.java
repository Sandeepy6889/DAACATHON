package com.siemens.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.siemens.primeCult.core.DbRowToObject;

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

	public static int insert(TableRow row) {
		int id = 0;
		Connection connection = null;
		InsertOperations inOperation = new InsertOperations();
		try {
			connection = DbConnection.getDbConnection();
			connection.setAutoCommit(false);
			id = inOperation.insert(connection, row);
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbConnection.releaseResources(connection);
		}
		return id;
	}
	
	public static boolean update(TableRow row) {
		boolean isUpdates = false;
		Connection connection = null;
		UpdateOperations inOperation = new UpdateOperations();
		try {
			connection = DbConnection.getDbConnection();
			connection.setAutoCommit(false);
			inOperation.update(connection, row);
			isUpdates = true;
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbConnection.releaseResources(connection);
		}
		return isUpdates;
	}
	
	public static boolean delete(TableRow row) {
		boolean isDeleted = false;
		Connection connection = null;
		DeleteOperations operation = new DeleteOperations();
		try {
			connection = DbConnection.getDbConnection();
			connection.setAutoCommit(false);
			operation.delete(connection, row);
			connection.commit();
			isDeleted = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbConnection.releaseResources(connection);
		}
		return isDeleted;
	}
}
