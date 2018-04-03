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
	public GenericEntity<List<Object>> getCalculatedAllKPI(@PathParam("assetId") String assetId, @PathParam("timeStamp") long beginTimeStamp) 
	{

		List<Object> kpiList = new ArrayList<>();
		long endTimeStamp = beginTimeStamp+600;
		String qString = "select * from reference_kpi where AssetId='" + assetId + "'and RefTimeStamp>="+beginTimeStamp+" and RefTimeStamp <= "+endTimeStamp+" order by RefTimeStamp";
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpReferencedKPIResultSet obj = new PumpReferencedKPIResultSet();
		List<Object> refList =dao.get(qString, obj);
		System.out.println(refList);
		
		String qString1 = "select * from Calculated_KPI where AssetId='" + assetId + "'and timestamp>="+beginTimeStamp+" and timestamp <= "+endTimeStamp+" order by timestamp";
		PumpKPIDAO dao1 = new PumpKPIDAO();
		PumpKPIResultSet obj1 = new PumpKPIResultSet();
		List<Object> calList =dao1.get(qString1, obj1);
		System.out.println("kpi : "+calList);
		 
		List<Object> tDHCalPoints = new ArrayList<>();
		List<Object> tDHRefPoints = new ArrayList<>();
		List<Object> effCalPoints = new ArrayList<>();
		List<Object> effRefPoints = new ArrayList<>();
		
		Iterator itrCal = calList.iterator();
		Iterator itrRef = refList.iterator();
		while (itrCal.hasNext() && itrRef.hasNext())
		{
			PumpKPI calKPI = (PumpKPI)itrCal.next();
			PumpReferencedKPI refKPI = (PumpReferencedKPI)itrRef.next();
			
			
			tDHCalPoints.add(new ChartPoint(calKPI.getTDH(),calKPI.getFlow()));
			tDHRefPoints.add(new ChartPoint(refKPI.getRefTDH(),refKPI.getRefFlow()));
			effCalPoints.add(new ChartPoint(calKPI.getEfficiency(),calKPI.getFlow()));
			effRefPoints.add(new ChartPoint(refKPI.getRefEfficiency(),refKPI.getRefFlow()));
			
		}
		
		kpiList.add(tDHCalPoints);
		kpiList.add(tDHRefPoints);
		kpiList.add(effCalPoints);
		kpiList.add(effRefPoints);
		
		GenericEntity<List<Object>> genericEntity = new GenericEntity<List<Object>>(tDHCalPoints) {};
		
		GenericEntity<List<Object>> genericEntity2 = new GenericEntity<List<Object>>(tDHRefPoints) {};
		GenericEntity<List<Object>> genericEntity3 = new GenericEntity<List<Object>>(effCalPoints) {};
		GenericEntity<List<Object>> genericEntity4 = new GenericEntity<List<Object>>(effRefPoints) {};
		
		List<Object> li = new ArrayList<>();
		li.add(genericEntity);
		li.add(genericEntity2);
		li.add(genericEntity3);
		li.add(genericEntity4);
		
		System.out.println(tDHCalPoints);
		System.out.println(tDHRefPoints);
		System.out.println(effCalPoints);
		System.out.println(effRefPoints);
		return new GenericEntity<List<Object>>(li) {};
	}

	
	@GET
	@Path("/referencedKPI/{assetId}/{timeStamp}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getReferencedAllKPI(@PathParam("assetId") String assetId, @PathParam("timeStamp") long beginTimeStamp) 
	{
		long endTimeStamp = beginTimeStamp+600;
		String qString = "select * from reference_kpi where AssetId='" + assetId + "'and RefTimeStamp>="+beginTimeStamp+" and RefTimeStamp <= "+endTimeStamp;
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpReferencedKPIResultSet obj = new PumpReferencedKPIResultSet();
		List<Object> list =dao.get(qString, obj);
		System.out.println("kpi Ref: "+list);
		return list;
	}
	
	
	public static void main(String[] args) {

		String assetId="assetid";
		long beginTimeStamp = 1522393670;
		long endTimeStamp = beginTimeStamp+600;
		String qString = "select * from reference_kpi where AssetId='" + assetId + "'and RefTimeStamp>="+beginTimeStamp+" and RefTimeStamp <= "+endTimeStamp+" order by RefTimeStamp";
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpReferencedKPIResultSet obj = new PumpReferencedKPIResultSet();
		List<Object> refList =dao.get(qString, obj);
		System.out.println(refList);
		
		String qString1 = "select * from Calculated_KPI where AssetId='" + assetId + "'and timestamp>="+beginTimeStamp+" and timestamp <= "+endTimeStamp+" order by timestamp";
		PumpKPIDAO dao1 = new PumpKPIDAO();
		PumpKPIResultSet obj1 = new PumpKPIResultSet();
		List<Object> calList =dao1.get(qString1, obj1);
		System.out.println("kpi : "+calList);
		
		ChartPoint []tDHCalPoints = new ChartPoint[calList.size()];
		ChartPoint []tDHRefPoints = new ChartPoint[calList.size()];
		ChartPoint []effCalPoints = new ChartPoint[calList.size()];
		ChartPoint []effRefPoints = new ChartPoint[calList.size()];
		
		Iterator itrCal = calList.iterator();
		Iterator itrRef = refList.iterator();
		int arrayCounter=0;
		while (itrCal.hasNext() && itrRef.hasNext())
		{
			PumpKPI calKPI = (PumpKPI)itrCal.next();
			PumpReferencedKPI refKPI = (PumpReferencedKPI)itrRef.next();
			
			tDHCalPoints[arrayCounter] = new ChartPoint(calKPI.getTDH(),calKPI.getFlow());
			tDHRefPoints[arrayCounter] = new ChartPoint(refKPI.getRefTDH(),refKPI.getRefFlow());
			effCalPoints[arrayCounter] = new ChartPoint(calKPI.getEfficiency(),calKPI.getFlow());
			effRefPoints[arrayCounter] = new ChartPoint(refKPI.getRefEfficiency(),refKPI.getRefFlow());
			arrayCounter++;
		}
		System.out.println(tDHCalPoints);
		System.out.println(tDHRefPoints);
		System.out.println(effCalPoints);
		System.out.println(effRefPoints);
	}
}
