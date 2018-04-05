package com.siemens.primeCult.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.siemens.storage.DbConnection;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class AlarmReport {
	public static void main(String[] args) throws JRException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		AlarmReport report = new AlarmReport();
		report.createReport();
	}
	
	public void createReport() throws JRException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("alarms-report.jrxml");
		JasperReport alarmReport = JasperCompileManager.compileReport(inputStream);
		Connection dbConnection = DbConnection.getDbConnection();
		
		Statement stmt=dbConnection.createStatement();
        String queryString = "select * from alarms where timestamp > 3234567801 and timestamp < 3234567811";
        ResultSet rset = stmt.executeQuery(queryString);
        JRResultSetDataSource jasperReports = new JRResultSetDataSource(rset);
		JasperPrint print = JasperFillManager.fillReport(alarmReport,null, jasperReports);
		
		File pdf = File.createTempFile("output.", ".pdf");
		System.out.println(pdf.getAbsolutePath()+" "+pdf.getName());
		JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(pdf));
		System.out.println("Done");
//		JasperViewer.viewReport(print);
		DbConnection.releaseResources(dbConnection);
		
		
		
	}
}
