package com.siemens.pumpMonitoringServices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.pumpMonitoring.core.ParameterizedData;

@Path("/assetPrmtz")
public class ParameterizedDataService {

	static List<ParameterizedData> data = new ArrayList<>();
	static {
		ParameterizedData pData = new ParameterizedData();
		pData.setAssetID("pump1");
		pData.setAssetName("CentrifugalPump");
		pData.setMinRatedFlowOfPump(10);
		pData.setMotorEfficiency(96);
		pData.setMotorRatedSpeed(20);
		pData.setRatedPower(10);
		pData.setThreadholdLT(10);
		pData.setWaterDensity(1);
		data.add(pData);
	}

	@GET
	@Path("/{paramID}")
	@Produces(MediaType.APPLICATION_JSON)
	public ParameterizedData getData() {
		ParameterizedData pData = new ParameterizedData();
		pData.setAssetID("pump1");
		pData.setAssetName("CentrifugalPump");
		pData.setMinRatedFlowOfPump(10);
		pData.setMotorEfficiency(96);
		pData.setMotorRatedSpeed(20);
		pData.setRatedPower(10);
		pData.setThreadholdLT(10);
		pData.setWaterDensity(1);
		return pData;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ParameterizedData> getAll() {
		return data;
	}

	@POST
	@Path("add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ParameterizedData add(ParameterizedData pumpData) {
		System.out.println("Object received : "+pumpData);
		data.add(pumpData);
		return pumpData;
	}

}
