package com.siemens.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateOperations {

	public void update(Connection connection, TableRow row) throws SQLException {
		Map<String, Integer> columnsMetaData = getTableMetaData(connection, row);
		update(connection, row, columnsMetaData);
	}

	public void update(Connection connection, TableRow row, Map<String, Integer> columnsMetaData) {
		StringBuilder query = new StringBuilder();
		query.append("update ").append(row.getTablename()).append(" set ");
		List<String> columnNames = row.getColumnNames();
		for (int i = 0; i < columnNames.size(); i++) {
			query.append(columnNames.get(i)).append("=").append("?");
			if (i != columnNames.size() - 1)
				query.append(",");
		}
		query.append(" where ");
		List<String> whereColumns = row.getWhereColumnNames();
		for (int i = 0; i < whereColumns.size(); i++) {
			query.append(whereColumns.get(i)).append("=").append("?");
			if (i != whereColumns.size() - 1)
				query.append(" AND ");
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = connection.prepareStatement(query.toString());
			String cName = null;
			int colIndex = 1;
			for (int index = 0; index < columnNames.size(); index++) {
				cName = columnNames.get(index);
				Integer columnType = columnsMetaData.get(cName);
				if (columnType == null)
					throw new IllegalArgumentException(
							"No such column exists : " + cName + ", table name : " + row.getTablename());
				setValue(columnType, cName, row.getValue(cName), pstmt, colIndex++);
			}
			for (int index = 0; index < whereColumns.size(); index++) {
				cName = whereColumns.get(index);
				Integer columnType = columnsMetaData.get(cName);
				if (columnType == null)
					throw new IllegalArgumentException(
							"No such column exists : " + cName + ", table name : " + row.getTablename());
				setValue(columnType, cName, row.getWhereValue(cName), pstmt, colIndex++);
			}
			int rowUpdated = pstmt.executeUpdate();
			System.out.println("Update count : " + rowUpdated);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setValue(int type, String columnName, Object columnValue, PreparedStatement pstmt, int index)
			throws NumberFormatException, SQLException {
		switch (type) {
		case Types.INTEGER:
			pstmt.setInt(index, (Integer) columnValue);
			break;
		case Types.NUMERIC:
		case Types.DECIMAL:
		case Types.REAL:
			double val = 0;
			if (columnValue instanceof Integer)
				val = ((Integer) columnValue).doubleValue();
			else if (columnValue instanceof Float)
				val = ((Float) columnValue).doubleValue();
			else
				val = ((Double) columnValue).doubleValue();
			pstmt.setDouble(index, Double.valueOf(val));
			break;
		case Types.VARCHAR:
			pstmt.setString(index, String.valueOf(columnValue));
			break;
		case Types.BIGINT:
			pstmt.setLong(index, (Long) columnValue);
			break;
		default:
			System.out.println(columnName + " not mapped to any value");
			break;
		}
	}

	public static void main(String[] args) {
		UpdateOperations operation = new UpdateOperations();
		TableRow row = new TableRow("alarms");
		row.setId(1);
		row.update("alarm_type", "TDH");
		row.update("alarm_status", 0);
		row.update("fluid_flow", 30.6);
		// row.update("timestamp", 1234567901l);
		// row.where("timestamp", 987654321l);
		row.where("alarm_status", 0);
		Connection dbConnection = null;
		try {
			dbConnection = DbConnection.getDbConnection();
			operation.update(dbConnection, row);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			DbConnection.releaseResources(dbConnection);
		}
	}

	private Map<String, Integer> getTableMetaData(Connection connection, TableRow row) throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rset = stmt.executeQuery("select * from " + row.getTablename());
		ResultSetMetaData metaData = rset.getMetaData();
		Map<String, Integer> columnsMetaData = new HashMap<>();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			columnsMetaData.put(metaData.getColumnName(i), metaData.getColumnType(i));
		}
		return columnsMetaData;
	}

}
