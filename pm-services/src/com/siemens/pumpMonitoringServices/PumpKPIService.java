package com.siemens.pumpMonitoringServices;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import com.siemens.dao.DBUtil;
import com.siemens.dao.PumpKPIDAO;
import com.siemens.pumpMonitoring.core.ChartPoint;
import com.siemens.pumpMonitoring.core.PumpKPI;
import com.siemens.pumpMonitoring.core.PumpKPIResultSet;
import com.siemens.pumpMonitoring.core.PumpReferencedKPI;
import com.siemens.pumpMonitoring.core.PumpReferencedKPIResultSet;

@Path("/kpi")
public class PumpKPIService {

	@GET
	@Path("/get")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAll() {
		String qStr = "SELECT * from Calculated_KPI";
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpKPIResultSet obj = new PumpKPIResultSet();
		return dao.get(qStr, obj);
	}
	
	@GET
	@Path("/calculatedKPI/{assetId}/{timeStamp}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<List<Double[]>> getCalculatedAllKPI(@PathParam("assetId") String assetId, @PathParam("timeStamp") long endTimeStamp) 
	{

		List<List<Double[]>> kpiList = new ArrayList<>();
		long beginTimeStamp = endTimeStamp-600;
		String qString = "select * from reference_kpi where AssetId='" + assetId + "'and RefTimeStamp>="+beginTimeStamp+" and RefTimeStamp <= "+endTimeStamp+" order by RefTimeStamp";
		System.out.println(qString);
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpReferencedKPIResultSet obj = new PumpReferencedKPIResultSet();
		List<Object> refList =dao.get(qString, obj);
		System.out.println(refList);
		
		String qString1 = "select * from Calculated_KPI where AssetId='" + assetId + "'and timestamp>="+beginTimeStamp+" and timestamp <= "+endTimeStamp+" order by timestamp";
		System.out.println(qString1);
		PumpKPIDAO dao1 = new PumpKPIDAO();
		PumpKPIResultSet obj1 = new PumpKPIResultSet();
		List<Object> calList =dao1.get(qString1, obj1);
		System.out.println("kpi : "+calList);
		 
		List<Double[]> tDHCalPoints = new ArrayList<>();
		List<Double[]> tDHRefPoints = new ArrayList<>();
		List<Double[]> effCalPoints = new ArrayList<>();
		List<Double[]> effRefPoints = new ArrayList<>();
		
		Iterator itrCal = calList.iterator();
		Iterator itrRef = refList.iterator();

		while (itrCal.hasNext() && itrRef.hasNext())
		{
			//double []tDHCalPoints = new double[2];
			PumpKPI calKPI = (PumpKPI)itrCal.next();
			PumpReferencedKPI refKPI = (PumpReferencedKPI)itrRef.next();
			tDHCalPoints.add(new Double[] {calKPI.getFlow(),calKPI.getTDH()});
			tDHRefPoints.add(new Double[] {refKPI.getRefFlow(),refKPI.getRefTDH()});
			effCalPoints.add(new Double[] {calKPI.getFlow(),calKPI.getEfficiency()});
			effRefPoints.add(new Double[] {refKPI.getRefFlow(),refKPI.getRefEfficiency()});
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
	public List<Integer> getReferencedAllKPI(@PathParam("assetId") String assetId) 
	{
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(0);
		list.add(1);
		list.add(0);
		System.out.println(list);
		return list;
	}
	
	
	public static void main(String[] args) {}
}
