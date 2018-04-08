package com.siemens.primeCult.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.siemens.storage.DbConnection;

import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class ReportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/pdf");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// set input and output stream
		ServletOutputStream servletOutputStream = response.getOutputStream();
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("alarms-report.jrxml");
		Connection dbConnection = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			JasperReport alarmReport = JasperCompileManager.compileReport(inputStream);
			dbConnection = DbConnection.getDbConnection();
			stmt = dbConnection.createStatement();
			String queryString = "select * from alarms where timestamp > 3234567801 and timestamp < 3234567811";
			rset = stmt.executeQuery(queryString);
			JRResultSetDataSource jasperReports = new JRResultSetDataSource(rset);
			JasperPrint print = JasperFillManager.fillReport(alarmReport, null, jasperReports);

			JasperExportManager.exportReportToPdfStream(print, baos);
			response.setContentLength(baos.size());
			baos.writeTo(servletOutputStream);

		} catch (Exception e) {
			System.out.println("Exception in report");
			e.printStackTrace();
		} finally {
			if (dbConnection != null) {
				DbConnection.releaseResources(dbConnection);
			}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (rset != null)
				try {
					rset.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			servletOutputStream.flush();
            servletOutputStream.close();
			baos.close();
		}

	}

}