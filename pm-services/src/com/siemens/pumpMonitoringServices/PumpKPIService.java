package com.siemens.pumpMonitoringServices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.dao.DBUtil;
import com.siemens.dao.PumpKPIDAO;
import com.siemens.pumpMonitoring.core.PumpKPIResultSet;
import com.siemens.pumpMonitoring.core.PumpReferencedKPIResultSet;
import com.siemens.pumpMonitoring.core.TrainingDataRecord;

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
	@Path("/{assetId}/{timeStamp}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getCalculatedAllKPI(@PathParam("assetId") String assetId, @PathParam("timeStamp") long beginTimeStamp) 
	{
		long endTimeStamp = beginTimeStamp+600;
		String qString = "select * from Calculated_KPI where AssetId='" + assetId + "'and timestamp>="+beginTimeStamp+" and timestamp <= "+endTimeStamp;
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpKPIResultSet obj = new PumpKPIResultSet();
		List<Object> list =dao.get(qString, obj);
		return list;
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
		return list;
	}
	
	
	public static void main(String[] args) {

		String assetId="assetid";
		long beginTimeStamp = 1522393670;
		long endTimeStamp = beginTimeStamp+600;
		String qString = "select * from reference_kpi where AssetId='" + assetId + "'and RefTimeStamp>="+beginTimeStamp+" and RefTimeStamp <= "+endTimeStamp;
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpReferencedKPIResultSet obj = new PumpReferencedKPIResultSet();
		List<Object> list =dao.get(qString, obj);
		System.out.println(list);
	}
}
