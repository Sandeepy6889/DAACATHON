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
	@Path("/{assetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAll(@PathParam("assetId") String assetId) {
		String qString = "select * from Calculated_KPI where AssetId='" + assetId + "'  ORDER BY TimeStamp DESC LIMIT 10" ;
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpKPIResultSet obj = new PumpKPIResultSet();
		return dao.get(qString, obj);
	}

	public static void main(String[] args) {

		String assetId="assetid";
		String qString = "select * from Calculated_KPI where AssetId='" + assetId + "'  ORDER BY TimeStamp DESC LIMIT 10" ;
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpKPIResultSet obj = new PumpKPIResultSet();
		List<Object> list =dao.get(qString, obj);
		System.out.println(list);
	}
}
