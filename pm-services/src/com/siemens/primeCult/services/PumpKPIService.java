package com.siemens.primeCult.services;

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

import com.siemens.dao.PumpKPIDAO;
import com.siemens.primeCult.core.PumpKPI;
import com.siemens.primeCult.core.PumpKPIResultSet;
import com.siemens.primeCult.core.PumpReferencedKPI;
import com.siemens.primeCult.core.PumpReferencedKPIResultSet;
import com.siemens.storage.DBUtil;
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

		long beginTimeStamp = endTimeStamp - 300000;
		PumpKPIDAO dao = new PumpKPIDAO();
		List<List<Double[]>> kpiList = new ArrayList<>();
		PumpKPIDAO dao1 = new PumpKPIDAO();
		PumpKPIResultSet obj1 = new PumpKPIResultSet();
		String qString1 = "select * from calculated_kpi where AssetId='" + assetId + "'and Timestamp>=" + beginTimeStamp
				+ " and Timestamp <= " + endTimeStamp + " order by Timestamp";
		List<Object> calList = dao1.get(qString1, obj1);

		String qString = "select * from refrence_kpi where AssetId='" + assetId + "'and Timestamp>=" + beginTimeStamp
				+ " and Timestamp <= " + endTimeStamp + " order by Timestamp";
		PumpReferencedKPIResultSet obj = new PumpReferencedKPIResultSet();
		List<Object> refList = dao.get(qString, obj);

		
		//System.out.println("KPI "+calList.size());
		//System.out.println("Ref KPI "+refList.size());

		List<Double[]> tDHCalPoints = new ArrayList<>();
		List<Double[]> tDHRefPoints = new ArrayList<>();
		List<Double[]> effCalPoints = new ArrayList<>();
		List<Double[]> effRefPoints = new ArrayList<>();
		
		Iterator<Object> itrCal = calList.iterator();
		Iterator<Object> itrRef = refList.iterator();

		while (itrCal.hasNext()) {
			PumpKPI calKPI = (PumpKPI) itrCal.next();
			tDHCalPoints.add(new Double[] { calKPI.getFlow(), calKPI.getTDH() });
			effCalPoints.add(new Double[] { calKPI.getFlow(), calKPI.getEfficiency() });
		}
		while (itrRef.hasNext()) {
			PumpReferencedKPI refKPI = (PumpReferencedKPI) itrRef.next();
			tDHRefPoints.add(new Double[] { refKPI.getRefFlow(), refKPI.getRefTDH() });
			effRefPoints.add(new Double[] { refKPI.getRefFlow(), refKPI.getRefEfficiency() });
		}

		kpiList.add(tDHCalPoints);
		kpiList.add(tDHRefPoints);
		kpiList.add(effCalPoints);
		kpiList.add(effRefPoints);
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
		list.add(1);
		return list;
	}

	public static void main(String[] args) {

		Random randon = new Random();
		for (int i = 20; i < 300; i++) {
			long time = new Date().getTime();
			TableRow row = new TableRow("calculated_kpi");
			row.add("AssetId", "PUMP01");
			row.add("Flow", i);
			row.add("TDH", randon.nextInt(40));
			row.add("Efficiency", randon.nextInt(40));
			row.add("Timestamp", time);
			TableRow rrow = new TableRow("refrence_kpi");
			rrow.add("AssetId", "PUMP01");
			rrow.add("Flow", i);
			rrow.add("TDH", randon.nextInt(90));
			rrow.add("Efficiency", randon.nextInt(20));
			rrow.add("Timestamp", time);
			
			DBUtil.insert(row);
			DBUtil.insert(rrow);
			System.out.println(i - 19 + " row calculate kpi inserted PUMP03");
			System.out.println(i - 19 + " row ref kpi inserted");
		}
	}
}
