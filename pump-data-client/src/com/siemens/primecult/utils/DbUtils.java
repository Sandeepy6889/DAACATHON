package com.siemens.primecult.utils;

import java.sql.Connection;
import java.sql.SQLException;

import com.siemens.primecult.oracle.OracleConnection;
import com.siemens.primecult.oracle.OracleDBOperation;

public class DbUtils {

	public static void insert(int id, String assetId, double ff, double ps, double pd, double pwr) {
		Connection connection = null;
		try {
			connection = OracleConnection.getDbConnection();
			connection.setAutoCommit(false);
			OracleDBOperation.insert(connection, id, assetId, ff, ps, pd, pwr);
			connection.commit();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("insert Failed for asset Id " + assetId + "-" + ff + "-" + ps + "-" + pd + "-" + pwr);
			e.printStackTrace();
			try {
				if (connection != null)
					connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			OracleConnection.releaseResources(connection);
		}
	}

	public static float[] getPumpDataValues(int id) {
		Connection connection = null;
		float[] pumpDataValues = new float[4];
		try {
			connection = OracleConnection.getDbConnection();
			System.out.println(id);
			pumpDataValues = OracleDBOperation.getPumpDataValues(connection, id);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			try {
				if (connection != null)
					connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			OracleConnection.releaseResources(connection);
		}
		return pumpDataValues;
	}

}
