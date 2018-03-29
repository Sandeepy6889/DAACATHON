package com.siemens.pumpMonitoringServices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.dao.ParameterizedDataDAO;
import com.siemens.dao.PumpKPIDAO;
import com.siemens.pumpMonitoring.core.ParameterizedDataResultSet;
import com.siemens.pumpMonitoring.core.PumpKPIResultSet;

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
	
	public static void main(String[] args) {
		String qStr = "SELECT * from Calculated_KPI";
		PumpKPIDAO dao = new PumpKPIDAO();
		PumpKPIResultSet obj = new PumpKPIResultSet();
		System.out.println(dao.get(qStr, obj));
	}
}
