package com.siemens.primecult.kpiServices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.primecult.models.ValueRt;

@Path("/KPI-Calculation")
public class KPICalculationService {

	@POST
	@Path("/calculate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ValueRt post(ValueRt requestData) {
		System.out.println(requestData);
		KPICalculator kpiCalculator = new KPICalculator();
		kpiCalculator.calculateKPI(requestData);
		return requestData;
	}
}
