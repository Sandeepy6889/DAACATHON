/*
 * Copyright (c) Siemens AG 2016 ALL RIGHTS RESERVED.
 *
 * SPPA-T3000  
 * 
 */
package com.siemens.storage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Provides the Method for Changing password in MYSQL DB.
 * 
 * @author Suhail Gupta
 * 
 */
public class MySQLAdaptor {
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
	public MySQLAdaptor() {
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void getDbConnection(Properties dbProperties) {
		String portNumber = dbProperties.getProperty(PORT).trim();
		String dbDriver = dbProperties.getProperty(DBDRIVER).trim();
		String dbName = dbProperties.getProperty(DB_NAME).trim();

		String strLoginUID = dbProperties.getProperty(USERNAME).trim();
		String strLoginPassword = dbProperties.getProperty(PASSWORD).trim();

		String strHostName = dbProperties.getProperty(HOSTNAME);
		Properties properties = getDBProperties(strLoginUID, strLoginPassword);
		String dbUrl = DEFAULT_DBURL + strHostName + ":" + portNumber + "/" + dbName;

		System.out.println("dbURL-" + dbUrl + "-");
		Connection mConnection = null;
		PreparedStatement preparedStatement = null;
		boolean connectionEstablished = false;
		try {

			Class.forName(dbDriver).newInstance();
			System.out.println("DB class loaded");
			mConnection = DriverManager.getConnection(dbUrl, properties);
			System.out.println("Connected successfully");
			connectionEstablished = true;

		} catch (Exception e) {
			System.out.println("unable to connect");
			e.printStackTrace();
		} finally {
			releaseResources(mConnection, preparedStatement);
		}
	}

	/**
	 * Release the resources
	 * 
	 * @param mConnection
	 *            Connection reference
	 * @param stmt
	 *            Statement reference
	 */
	private void releaseResources(Connection mConnection, PreparedStatement stmt) {
		try {
			if (stmt != null)
				stmt.close();
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
	private Properties getDBProperties(String strLoginUID, String strLoginPassword) {
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

	public void loadFile() {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("database.cfg");
		Properties dbProperties = new Properties();
		try {
			dbProperties.load(inputStream);
			getDbConnection(dbProperties);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		MySQLAdaptor adt = new MySQLAdaptor();
		adt.loadFile();
	}

	/*
	 * 
	 * ## MYSQL DB Properties MYSQL_DBDRIVER = com.mysql.jdbc.Driver MYSQL_DB_PORT =
	 * 3306 MYSQL_DB_NAME = sandeep MYSQL_USERNAME = sandeep MYSQL_PASSWORD =
	 * sandeepyadav MYSQL_HOSTNAME =
	 * pump-testing-75019790.cnpdj3nbkprg.us-east-1.rds.amazonaws.com
	 */

}
/*
 * Copyright (c) Siemens AG 2016 ALL RIGHTS RESERVED.
 * 
 * SPPA-T3000
 */
