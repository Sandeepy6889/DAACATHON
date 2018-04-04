package com.siemens.primecult.kpiServices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.primecult.alarmservices.AlarmManagement;
import com.siemens.primecult.core.KPICalculator;
import com.siemens.primecult.models.ValueRt;

@Path("/KPI-Calculation")
public class KPICalculationService {

	@POST
	@Path("/calculate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ValueRt post(ValueRt requestData) {
		KPICalculator kpiCalculator = new KPICalculator();
		kpiCalculator.calculateKPI(requestData);
		return requestData;
	}
	
	@GET
	@Path("/assetTrainStatus/{assetId}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String changeTrainingStatus(@PathParam("assetId") String assetId) {
		return KPICalculator.changeTrainingStatus(assetId);
	}
	
	@GET
	@Path("/alarmStatus/{assetId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public List<Integer> getAlarmStatus(@PathParam("assetId") String assetId) {
		return AlarmManagement.getCurrentAlarmStatus(assetId);
	}
	
	@GET
	@Path("/assetcreated/{assetId}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String createCache(@PathParam("assetId") String assetId) {
	    AlarmManagement.makeEntryToManageAlarmsForAsset(assetId);
	    KPICalculator.makeEntryToManageAssetTrainingStatus(assetId);
	    return "success";
	}
}
