package com.siemens.primecult.services;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
		return requestData;
	}
	
	
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String getKPI() {
		//Object obj = new ObjectToJSonMapper().mapJSonStrToObj(plainText,ValueRt.class);
		System.out.println("HELLO");
		return "Hello";
	}
}
