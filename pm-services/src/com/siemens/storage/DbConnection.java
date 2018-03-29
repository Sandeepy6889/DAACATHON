/*
 * Copyright (c) Siemens AG 2016 ALL RIGHTS RESERVED.
 *
 * SPPA-T3000  
 * 
 */
package com.siemens.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import com.siemens.pumpMonitoring.core.Configuration;

/**
 * Provides the Method for Changing password in MYSQL DB.
 * 
 */
public class DbConnection {
	private static final String DEFAULT_DBURL = "jdbc:mysql://";
	private static final String DB_NAME = "MYSQL_DB_NAME";
	private static final String PORT = "MYSQL_DB_PORT";
	private static final String DBDRIVER = "MYSQL_DBDRIVER";
	private static final String USERNAME = "MYSQL_USERNAME";
	private static final String PASSWORD = "MYSQL_PASSWORD";
	private static final String HOSTNAME = "MYSQL_HOSTNAME";

	/**
	 * 
	 * @param sPSPropertiesReader
	 *            SPSPropertiesReader reference
	 */
	public DbConnection() {
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Connection dbConnection = getDbConnection();
		System.out.println(dbConnection);
		releaseResources(dbConnection);
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SQLException
	 */
	public static Connection getDbConnection()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Properties dbProperties = Configuration.getDbProperties();
		String portNumber = dbProperties.getProperty(PORT).trim();
		String dbDriver = dbProperties.getProperty(DBDRIVER).trim();
		String dbName = dbProperties.getProperty(DB_NAME).trim();

		String strLoginUID = dbProperties.getProperty(USERNAME).trim();
		String strLoginPassword = dbProperties.getProperty(PASSWORD).trim();

		String strHostName = dbProperties.getProperty(HOSTNAME);
		Properties properties = getDBProperties(strLoginUID, strLoginPassword);
		 String dbUrl = DEFAULT_DBURL + strHostName + ":" + portNumber + "/" + dbName;
		//String dbUrl = "jdbc:oracle:thin:@localhost:1521:xe";
		// System.out.println("dbURL-" + dbUrl + "-");
		Connection mConnection = null;
		Class.forName(dbDriver).newInstance();
		mConnection = DriverManager.getConnection(dbUrl, properties);
		return mConnection;
	}

	/**
	 * Release the resources
	 * 
	 * @param mConnection
	 *            Connection reference
	 * @param stmt
	 *            Statement reference
	 */
	public static void releaseResources(Connection mConnection) {
		try {
			if (mConnection != null)
				mConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get DB Properties.
	 * 
	 * @param strLoginUID
	 *            loginUserId to make DB connection.
	 * @param strLoginPassword
	 *            loginPassword to make DB connection.
	 * @return Properties
	 */
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
/*
 * Copyright (c) Siemens AG 2016 ALL RIGHTS RESERVED.
 * 
 * SPPA-T3000
 */
