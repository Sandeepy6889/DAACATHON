package com.siemens.primeCult.services;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.primeCult.core.ParameterizedData;
import com.siemens.storage.DBUtil;
import com.siemens.storage.TableRow;

@Path("/assetPrmtz")
public class ParameterizedDataService {

	@GET
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAll() {
		String qStr = "SELECT * from paramdata";
		ParameterizedData obj = new ParameterizedData();
		return DBUtil.get(qStr, obj);
	}

	@POST
	@Path("/insert")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ParameterizedData add(ParameterizedData pumpData) {
		TableRow row = new TableRow("paramdata");
		row.add("assetid ", pumpData.getAssetID());
		row.add("assetname ", pumpData.getAssetName());
		row.add("ratedpower", pumpData.getRatedPower());
		row.add("motorefficiency", pumpData.getMotorEfficiency());
		row.add("motorratedspeed", pumpData.getMotorRatedSpeed());
		row.add("minratedflowofpump", pumpData.getMinRatedFlowOfPump());
		row.add("waterdensity", pumpData.getWaterDensity());
		row.add("threslt", pumpData.getThreadholdLT());
		row.add("suctiondiameter", pumpData.getSuctionDiameter());
		row.add("dischargediameter", pumpData.getDischargeDiameter());
		row.add("eleveationdiff", pumpData.getEleveationDiff());

		int id = DBUtil.insert(row);
		System.out.println("id : "+id);
		boolean isSuccess = id == -1;
		if (isSuccess)
			return pumpData;
		else
			return null;
	}
	
	@GET
	@Path("/delete/{assetId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String delete(@PathParam("assetId") String assetId) {
		TableRow row = new TableRow("paramdata");
		row.where("assetid", assetId);
		boolean isSuccess = DBUtil.delete(row);
		System.out.println("Deleted : "+isSuccess+" id "+assetId);
		if (isSuccess)
			return "SUCCESS";
		else
			return "FAIL";
	}
}
