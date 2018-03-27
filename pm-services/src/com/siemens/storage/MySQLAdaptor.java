/*
 * Copyright (c) Siemens AG 2016 ALL RIGHTS RESERVED.
 *
 * SPPA-T3000  
 * 
 
package com.siemens.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.pg.orion.sps.config.SPSPropertiesReader;
import com.pg.orion.sps.interfaces.IAdapter;
import com.pg.orion.sps.logger.FormatLogger;
import com.pg.orion.sps.model.OprStatus;
import com.pg.orion.sps.model.SPSAccountPwdChg;
import com.pg.orion.sps.model.SPSDefaultMessage;
import com.pg.orion.sps.util.SPSErrorCode;

*//**
 * Provides the Method for Changing password in MYSQL DB.
 * 
 * @author Suhail Gupta
 * 
 *//*
public class MySQLAdaptor implements IAdapter
{
	private static final FormatLogger LOGGER = new FormatLogger(Logger.getLogger(MySQLAdaptor.class));
    private SPSPropertiesReader mSPSPropertiesReader;
    private static final String DEFAULT_DBURL = "jdbc:mysql://";
    private static final String DB_NAME_PROPERTY_NAME = "MYSQL_DB_NAME";
    private static final String DB_PORT_PROPERTY_NAME = "MYSQL_DB_PORT";
    private static final String DBDRIVER_PROPERTY_NAME = "MYSQL_DBDRIVER";
    
    *//**
     * 
     * @param sPSPropertiesReader SPSPropertiesReader reference
     *//*
    public MySQLAdaptor(SPSPropertiesReader sPSPropertiesReader)
    {
        mSPSPropertiesReader = sPSPropertiesReader;
    }
    
    *//**
     * 
     * {@inheritDoc}
     *//*
    @Override
    public OprStatus changePwd(SPSAccountPwdChg objRecordPwdChg)
    {
    	LOGGER.info("changePwd ->");
        
        String updatePasswdSuccess = SPSDefaultMessage.getUpdatePasswdSuccessMessage();
        String passwdErrorMessage = SPSDefaultMessage.getUpdatePasswdErrorMessage();
        String connectionNotEstablishedMessage = SPSDefaultMessage.getDBConnectionErrorMessage();
        String actualUser=null;
        OprStatus oprStatus = new OprStatus(SPSErrorCode.NO_ERROR, updatePasswdSuccess);
        String portNumber = mSPSPropertiesReader.getProperty(DB_PORT_PROPERTY_NAME);
        String dbDriver = mSPSPropertiesReader.getProperty(DBDRIVER_PROPERTY_NAME);
        String dbName = mSPSPropertiesReader.getProperty(DB_NAME_PROPERTY_NAME);
        
        String actualDBHostName="localhost";
        String strLoginUID = objRecordPwdChg.getLoginUID();
        String strLoginPassword = objRecordPwdChg.getLoginPasswd();
        String strAccountUID = objRecordPwdChg.getAccountUID();
        if(strAccountUID.contains("@"))
        {
            String[] accountSplit = strAccountUID.split("@");
            actualUser=accountSplit[0];
            actualDBHostName=accountSplit[1];
        }
        String strHostName = objRecordPwdChg.getHostName();
        String strNewPassword = objRecordPwdChg.getNewPassword();
        Properties properties = getDBProperties(strLoginUID, strLoginPassword);
        String dbUrl = DEFAULT_DBURL + strHostName + ":" + portNumber + "/" + dbName;

        LOGGER.trace("changePwd -> DB URL : %s ", dbUrl);
        
        Connection mConnection = null;
        PreparedStatement preparedStatement=null;
        boolean connectionEstablished = false;
        try
        {
            Class.forName(dbDriver).newInstance();
            mConnection = getDBConnection(properties, dbUrl);
            LOGGER.debug("changePwd -> DB Connection established to host : %s ", strHostName);
            connectionEstablished = true;
            String sqlStatement = getQuery(actualUser, strHostName, strNewPassword);
            preparedStatement=mConnection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, strAccountUID);
            preparedStatement.setString(2, actualDBHostName);
            preparedStatement.setString(3, strNewPassword);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
        	LOGGER.error("changePasswd -> SQLException Occured Error Message : %s ,Error Code : %d", e.getMessage(),e.getErrorCode());
            if (!connectionEstablished)
            {
                oprStatus.setErrorCode(SPSErrorCode.DB_CONNECTION_ERROR);
                oprStatus.setErrorMessage(connectionNotEstablishedMessage);
            }
            else
            {
                oprStatus.setErrorCode(SPSErrorCode.SUBSYSTEM_ERROR);
                oprStatus.setErrorMessage(passwdErrorMessage);
            }
        }
        catch (Exception e)
        {
        	LOGGER.error("changePasswd ->  Exception Occured : %s", e.getMessage());
            oprStatus.setErrorCode(SPSErrorCode.SUBSYSTEM_ERROR);
            oprStatus.setErrorMessage(passwdErrorMessage);
        }
        finally
        {
            releaseResources(mConnection, preparedStatement);
        }
        LOGGER.info("changePwd <- return");
        return oprStatus;
    }
    
    *//**
     * Get the DB connection.
     * 
     * @param properties Properties reference
     * @param dbUrl dbUrl String
     * @return Connection reference
     * @throws SQLException SQLException exception
     *//*
    protected Connection getDBConnection(Properties properties, String dbUrl) throws SQLException
    {
        return DriverManager.getConnection(dbUrl, properties);
    }
    
    *//**
     * Release the resources
     * 
     * @param mConnection Connection reference
     * @param stmt Statement reference
     *//*
    private void releaseResources(Connection mConnection, PreparedStatement stmt)
    {
        try
        {
            if (stmt != null)
                stmt.close();
            if (mConnection != null)
                mConnection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    *//**
     * Get DB Properties.
     * 
     * @param strLoginUID loginUserId to make DB connection.
     * @param strLoginPassword loginPassword to make DB connection.
     * @return Properties
     *//*
    private Properties getDBProperties(String strLoginUID,String strLoginPassword)
    {
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
    
    *//**
     * Get DB Query
     * 
     * @param strAccountUID accountUserId String
     * @param strHostName account HostIP String
     * @param strNewPassword account NewPassword
     * @return query String.
     *//*
    private String getQuery(String strAccountUID, String strHostName, String strNewPassword)
    {
        
        String query = "SET PASSWORD FOR ?@? = PASSWORD(?)";
        return query;
    }
    
}

 * Copyright (c) Siemens AG 2016 ALL RIGHTS RESERVED.
 * 
 * SPPA-T3000
 
*/