package com.siemens.primeCult.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.siemens.primeCult.core.Alarm;
import com.siemens.storage.DBUtil;
import com.siemens.storage.DbConnection;

import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Path("/alarms")
public class AlarmReportServices {

	@GET
	@Path("/getAlarms/{assetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAll(@PathParam("assetId") String assetId) {
		String qString = "select * from alarms where Asset_id='" + assetId + "'";
		List<Object> records = DBUtil.get(qString, new Alarm());
		return records;
	}
	
	@GET
	@Path("/report/download")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile() {
		
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("alarms-report.jrxml");
		Connection dbConnection = null;
		Statement stmt = null;
		ResultSet rset = null;
		File alarmreport = null;
		try {
		JasperReport alarmReport = JasperCompileManager.compileReport(inputStream);
		dbConnection = DbConnection.getDbConnection();
		stmt=dbConnection.createStatement();
        String queryString = "select * from alarms where timestamp > 3234567801 and timestamp < 3234567811";
        rset = stmt.executeQuery(queryString);
        JRResultSetDataSource jasperReports = new JRResultSetDataSource(rset);
		JasperPrint print = JasperFillManager.fillReport(alarmReport,null, jasperReports);
		
		alarmreport = new File(getProjectPath()+"alarm-report.pdf");
		//System.out.println(alarmreport.getAbsolutePath()+" "+alarmreport.getName());
		JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(alarmreport));
		}catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(dbConnection != null) {
				DbConnection.releaseResources(dbConnection);
			}
		}
		System.out.println("Done");
		

	  return Response.ok(alarmreport, MediaType.APPLICATION_OCTET_STREAM)
	      .header("Content-Disposition", "attachment; filename=\"" + alarmreport.getName() + "\"" ) //optional
	      .build();
	      
	}
	
	private String getProjectPath() {
		String path = this.getClass().getClassLoader().getResource("").getPath();
		String fullPath = "";
		try {
			fullPath = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String pathArr[] = fullPath.split("/WEB-INF/classes/");
	//	System.out.println(fullPath);
		//System.out.println(pathArr[0]);
		fullPath = pathArr[0];
		String reponsePath = "";
		// to read a file from webcontent
		reponsePath = new File(fullPath).getPath() + File.separatorChar;
		return reponsePath;
	}
	
	public static void main(String[] args) {
		String qString = "select * from alarms where Asset_id='" + "pump1" + "'";
		List<Object> records = DBUtil.get(qString, new Alarm());
		System.out.println(records);
	}
	
}
