package com.siemens.storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SelectOperations {

	public void select(Connection connection, String qString, DbRowToObject dbResult) throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs;
		rs = stmt.executeQuery(qString);
		while (rs.next()) {
			dbResult.fill(rs);
		}
	}

	public static void main(String[] args) {
		SelectOperations op = new SelectOperations();
		op.execute();
	}

	private void execute() {
		/*
		 * Connection connection = null; TableRow row = new TableRow("daac");
		 * row.set("assetid ", "ratess", "intval"); ParameterizedDataResultSet obj =
		 * null; try { connection = DbConnection.getDbConnection(); obj = new
		 * ParameterizedDataResultSet(); String qStr = "SELECT * from paramdata";
		 * select(connection, qStr, obj); } catch (SQLException e) {
		 * e.printStackTrace(); } catch (Exception e) { e.printStackTrace(); } finally {
		 * DbConnection.releaseResources(connection); }
		 * 
		 * List<Object> data = obj.getData(); for (Object parameterizedData : data) {
		 * System.out.println(parameterizedData); }
		 */

	}
}
