package com.siemens.pumpMonitoringServices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.dao.DBUtil;
import com.siemens.pumpMonitoring.core.TrainingDataRecord;

@Path("/modelTraining")
public class TrainingDataService {

	static List<TrainingDataRecord> data = new ArrayList<>();
	static {
		TrainingDataRecord record = new TrainingDataRecord();
		record.setxFlow(50);
		record.setyEta(10);
		record.setyHeight(20);
		data.add(record);
	}
	
	@GET
	@Path("/getAssetsIds")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAllAssetsIDs() {
		String qString = "select * from paramdata";
		return DBUtil.getColumnValues(qString, "assetid");
	}
	
	@GET
	@Path("/getAssetTrainingData/{assetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAll(@PathParam("assetId") String assetId) {
		String qString = "select * from Training_Data where AssetId='" + assetId+"'";
		List<Object> records = DBUtil.get(qString, new TrainingDataRecord());
		return records;
	}
	
	@POST
	@Path("/insert")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TrainingDataRecord add(TrainingDataRecord trainingRecord) {
		data.add(trainingRecord);
		return trainingRecord;
	}
}
