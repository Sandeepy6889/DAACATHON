package com.siemens.pumpMonitoringServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.dao.DBUtil;
import com.siemens.dao.PumpKPIDAO;
import com.siemens.pumpMonitoring.core.PumpKPI;
import com.siemens.pumpMonitoring.core.PumpKPIResultSet;
import com.siemens.pumpMonitoring.core.PumpReferencedKPI;
import com.siemens.pumpMonitoring.core.PumpReferencedKPIResultSet;
import com.siemens.storage.DbConnection;
import com.siemens.storage.TableRow;

@Path("/kpi")
public class PumpKPIService {

	@GET
	@Path("/get")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAll() {
		String qStr = "SELECT * from calculated_kpi";
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpKPIResultSet obj = new PumpKPIResultSet();
		return dao.get(qStr, obj);
	}

	@GET
	@Path("/calculatedKPI/{assetId}/{timeStamp}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<List<Double[]>> getCalculatedAllKPI(@PathParam("assetId") String assetId,
			@PathParam("timeStamp") long endTimeStamp) {

		List<List<Double[]>> kpiList = new ArrayList<>();
		long beginTimeStamp = endTimeStamp-20;
		//insertRows(beginTimeStamp, assetId);
		String qString = "select * from refrence_kpi where AssetId='" + assetId + "'and Timestmp>="
				+ beginTimeStamp + " and Timestmp <= " + endTimeStamp + " order by Timestmp";
		System.out.println(qString);
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpReferencedKPIResultSet obj = new PumpReferencedKPIResultSet();
		List<Object> refList = dao.get(qString, obj);
	//	System.out.println(refList);

		String qString1 = "select * from calculated_kpi where AssetId='" + assetId + "'and timestamp>=" + beginTimeStamp
				+ " and timestamp <= " + endTimeStamp + " order by timestamp";
		System.out.println(qString1);
		PumpKPIDAO dao1 = new PumpKPIDAO();
		PumpKPIResultSet obj1 = new PumpKPIResultSet();
		List<Object> calList = dao1.get(qString1, obj1);
	//	System.out.println("kpi : " + calList);

		List<Double[]> tDHCalPoints = new ArrayList<>();
		List<Double[]> tDHRefPoints = new ArrayList<>();
		List<Double[]> effCalPoints = new ArrayList<>();
		List<Double[]> effRefPoints = new ArrayList<>();

		Iterator itrCal = calList.iterator();
		Iterator itrRef = refList.iterator();

		while (itrCal.hasNext()) {
			// double []tDHCalPoints = new double[2];
			PumpKPI calKPI = (PumpKPI) itrCal.next();
			PumpReferencedKPI refKPI = (PumpReferencedKPI) itrRef.next();
			tDHCalPoints.add(new Double[] { calKPI.getFlow(), calKPI.getTDH() });
			tDHRefPoints.add(new Double[] { refKPI.getRefFlow(), refKPI.getRefTDH() });
			effCalPoints.add(new Double[] { calKPI.getFlow(), calKPI.getEfficiency() });
			effRefPoints.add(new Double[] { refKPI.getRefFlow(), refKPI.getRefEfficiency() });
		}

		kpiList.add(tDHCalPoints);
		kpiList.add(tDHRefPoints);
		kpiList.add(effCalPoints);
		kpiList.add(effRefPoints);

		System.out.println(tDHCalPoints);
		System.out.println(tDHRefPoints);
		System.out.println(effCalPoints);
		System.out.println(effRefPoints);
		return kpiList;
	}

	@GET
	@Path("/getAlarmStatus/{assetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getReferencedAllKPI(@PathParam("assetId") String assetId) {
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(0);
		list.add(1);
		list.add(0);
		System.out.println(list);
		return list;
	}

	public static void main(String[] args) {

		Random random = new Random();
		long time = new Date().getTime() + 10000;
		Connection conn = null;
		try {
			conn = DbConnection.getDbConnection();
			PreparedStatement pstmt = conn.prepareStatement(
					"insert into calculated_kpi(AssetId,Flow,TDH,Efficiency,Timestamp) values(?,?,?,?,?)");

			// Set auto-commit to false
			conn.setAutoCommit(false);

			for (int i = 0; i < 300; i++) {
				// Set the variables
				pstmt.setString(1, "pump1");
				pstmt.setDouble(2, random.nextInt(30));
				pstmt.setDouble(3, random.nextInt(30));
				pstmt.setDouble(4, random.nextInt(30));
				pstmt.setLong(5, time + i);
				pstmt.addBatch();
			}
			System.out.println("Insert into database");
			int[] count = pstmt.executeBatch();
			System.out.println("Row inserted "+count.length);
			// Explicitly commit statements to apply changes
			conn.commit();
			pstmt.close();

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if(conn != null)
				DbConnection.releaseResources(conn);
		}

	}
	public static void insertRows(long beginTimestamp, String assetId) {

		
		Connection conn = null;
		try {
			conn = DbConnection.getDbConnection();
			PreparedStatement pstmt = conn.prepareStatement(
					"insert into calculated_kpi(AssetId,Flow,TDH,Efficiency,Timestamp) values(?,?,?,?,?)");
			PreparedStatement pstmtRef = conn.prepareStatement(
					"insert into refrence_kpi(AssetId,RefFlow,RefTDH,RefEfficiency,Timestmp) values(?,?,?,?,?)");

			// Set auto-commit to false
			conn.setAutoCommit(false);

			long counter =beginTimestamp;
			double tdh =getMaxFlow();
			for (int i = 0; i < 20; i++) {
				
				// Set the variables
				pstmt.setString(1, assetId);
				pstmt.setDouble(2, tdh);
				pstmt.setDouble(3, tdh);
				pstmt.setDouble(4, tdh);
				pstmt.setLong(5, counter);
				pstmt.addBatch();
				
				// Set the variables
				pstmtRef.setString(1, assetId);
				pstmtRef.setDouble(2, tdh+5);
				pstmtRef.setDouble(3, tdh+5);
				pstmtRef.setDouble(4, tdh+5);
				pstmtRef.setLong(5, counter);
				pstmtRef.addBatch();
				counter=counter+1;
				tdh=tdh+2;
			}
			System.out.println("Insert into database");
			int[] count = pstmt.executeBatch();
			System.out.println("Row inserted "+count.length);
			
			System.out.println("Insert into database");
			int[] countRef = pstmtRef.executeBatch();
			System.out.println("Row inserted "+countRef.length);
			
			// Explicitly commit statements to apply changes
			conn.commit();
			pstmt.close();

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if(conn != null)
				DbConnection.releaseResources(conn);
		}

	}
	
public static double getMaxFlow() {

		
		Connection conn = null;
		try {
			conn = DbConnection.getDbConnection();
			Statement stmt=conn.createStatement();  
			ResultSet rs=stmt.executeQuery("select max(flow) Flow from calculated_kpi;");  
			if(rs.next()) {
				return rs.getDouble("Flow");
			}
			else {
				return 2;
			} 
			

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if(conn != null)
				
				DbConnection.releaseResources(conn);
		}
		return 2;

	}
	
	
	
}
