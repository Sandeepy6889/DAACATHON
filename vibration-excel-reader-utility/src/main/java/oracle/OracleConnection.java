/*
 * Copyright (c) Siemens AG 2016 ALL RIGHTS RESERVED.
 *
 * SPPA-T3000  
 * 
 */
package oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class OracleConnection {
	private static final String DBDRIVER = "DBDRIVER";
	private static final String USERNAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
	private static final String HOSTNAME = "HOSTNAME";

	public OracleConnection() {
	}

	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Connection dbConnection = getDbConnection();
		System.out.println(dbConnection);
		releaseResources(dbConnection);
	}

	public static Connection getDbConnection()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Properties dbProperties = OracleConfiguration.getDbProperties();
		String dbDriver = dbProperties.getProperty(DBDRIVER).trim();
		String strLoginUID = dbProperties.getProperty(USERNAME).trim();
		String strLoginPassword = dbProperties.getProperty(PASSWORD).trim();
		String dbUrl = dbProperties.getProperty(HOSTNAME);
		Properties properties = getDBProperties(strLoginUID, strLoginPassword);
		Connection mConnection = null;
		Class.forName(dbDriver).newInstance();
		mConnection = DriverManager.getConnection(dbUrl, properties);
		return mConnection;
	}

	public static void releaseResources(Connection mConnection) {
		try {
			if (mConnection != null)
				mConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static Properties getDBProperties(String strLoginUID, String strLoginPassword) {
		Properties properties = new Properties();
		properties.put("user", strLoginUID);
		properties.put("password", strLoginPassword);
		properties.put("useUnicode", "true");
		properties.put("characterEncoding", "UTF-8");

		// the following property avoids a com.mysql.jdbc.MysqlDataTruncation
		// when a float value
		// will be inserted which is greater than the max. float value in MySQL
		properties.put("jdbcCompliantTruncation", "false");
		return properties;
	}

}
