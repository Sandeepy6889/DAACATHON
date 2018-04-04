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

public class DeleteOperations {

	public void delete(Connection connection, TableRow row) throws SQLException {
		Map<String, Integer> columnsMetaData = getTableMetaData(connection, row);
		delete(connection, row, columnsMetaData);
	}

	public void delete(Connection connection, TableRow row, Map<String, Integer> columnsMetaData) {
		StringBuilder query = new StringBuilder();
		query.append("delete from ").append(row.getTablename()).append(" where ");
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
			for (int i = 0; i < whereColumns.size(); i++) {
				cName = whereColumns.get(i);
				Integer columnType = columnsMetaData.get(cName);
				if (columnType == null)
					throw new IllegalArgumentException(
							"No such column exists : " + cName + ", table name : " + row.getTablename());
				setValue(columnType, cName, row.getWhereValue(cName), pstmt, i + 1);
			}
			System.out.println("Affected rows : " + pstmt.executeUpdate());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

	public static void main(String[] args) {
		DeleteOperations operation = new DeleteOperations();
		TableRow row = new TableRow("alarms");
		row.where("id", 1);
		Connection dbConnection = null;
		try {
			dbConnection = DbConnection.getDbConnection();
			operation.delete(dbConnection, row);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			DbConnection.releaseResources(dbConnection);
		}
	}
}
