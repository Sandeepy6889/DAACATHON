package com.siemens.pumpMonitoringServices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.dao.ParameterizedDataDAO;
import com.siemens.pumpMonitoring.core.ParameterizedData;
import com.siemens.storage.TableRow;

@Path("/assetPrmtz")
public class ParameterizedDataService {

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
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAll() {
		String qStr = "SELECT * from paramdata";
		ParameterizedDataDAO dao = new ParameterizedDataDAO();
		ParameterizedData obj = new ParameterizedData();
		return dao.get(qStr, obj);
	}

	@POST
	@Path("/insert")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ParameterizedData add(ParameterizedData pumpData) {
		TableRow row = new TableRow("paramdata");
		row.set("assetid ", pumpData.getAssetID());
		row.set("assetname ", pumpData.getAssetName());
		row.set("ratedpower", pumpData.getRatedPower());
		row.set("motorefficiency", pumpData.getMotorEfficiency());
		row.set("motorratedspeed", pumpData.getMotorRatedSpeed());
		row.set("minratedflowofpump", pumpData.getMinRatedFlowOfPump());
		row.set("waterdensity", pumpData.getWaterDensity());
		row.set("threslt", pumpData.getThreadholdLT());
		row.set("suctiondiameter", pumpData.getSuctionDiameter());
		row.set("dischargediameter", pumpData.getDischargeDiameter());
		row.set("eleveationdiff", pumpData.getEleveationDiff());

		ParameterizedDataDAO dao = new ParameterizedDataDAO();
		boolean isSuccess = dao.insert(row);
		if (isSuccess)
			return pumpData;
		else
			return null;
	}

}
