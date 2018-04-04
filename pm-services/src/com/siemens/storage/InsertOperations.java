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

public class InsertOperations {

	public void insert(Connection connection, TableRow row) throws SQLException {
		Map<String, Integer> columnsMetaData = getTableMetaData(connection, row);
		insert(connection, row, columnsMetaData);
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

	private void insert(Connection connection, TableRow row, Map<String, Integer> columnsMetaData)
			throws NumberFormatException, SQLException {
		StringBuilder query = new StringBuilder();
		StringBuilder params = new StringBuilder(") values(");
		query.append("insert into ").append(row.getTablename()).append("(");
		List<String> columnNames = row.getColumnNames();
		for (int i = 0; i < columnNames.size(); i++) {
			query.append(columnNames.get(i));
			params.append("?");
			if (!(columnNames.size() - 1 == i)) {
				query.append(",");
				params.append(",");
			}
		}
		String qString = query.append(params).append(")").toString();
		PreparedStatement pstmt = connection.prepareStatement(qString);
		String cName = null;
		for (int i = 0; i < columnNames.size(); i++) {
			cName = columnNames.get(i);
			Integer columnType = columnsMetaData.get(cName);
			if (columnType == null)
				throw new IllegalArgumentException(
						"No such column exists : " + cName + ", table name : " + row.getTablename());
			setValue(columnType, cName, row.getValue(cName), pstmt, i + 1);
		}
		System.out.println("Affected rows : " + pstmt.executeUpdate());

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
			pstmt.setLong(index, (Long)columnValue);
			break;
		default:
			System.out.println(columnName + " not mapped to any value");
			break;
		}
	}

	private void execute() {
		Connection connection = null;
		TableRow row = new TableRow("daac");
		row.set("assetid ", "pump6");
		row.set("ratess", 10.9f);
		row.set("intval", 10);
		try {
			connection = DbConnection.getDbConnection();
			for (int i = 0; i < 5; i++) {
				insert(connection, createRow());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbConnection.releaseResources(connection);
		}
	}

	public static void main(String[] args) {
		InsertOperations op = new InsertOperations();
		op.execute();
	}

	private int i = 0;

	private TableRow createRow() {
		TableRow row = new TableRow("paramdata");
		row.set("assetid ", "pumpMon" + i);
		row.set("assetname ", "assetname" + i);
		row.set("ratedpower", 10.9f + i);
		row.set("motorefficiency", 16.9f + i);
		row.set("motorratedspeed", 54.9f + i);
		row.set("minratedflowofpump", 43.9f + i);
		row.set("waterdensity", 30.9f + i);
		row.set("threslt", 0.8);
		i++;
		return row;
	}

	/*
	 * create table paramdata(assetid varchar2(100) primary key,assetname
	 * varchar2(100), ratedpower number(10,2),motorefficiency number(10,2),
	 * motorratedspeed number(10,2),minratedflowofpump number(10,2),waterdensity
	 * number(10,2),threslt number(10,2));
	 */
}
