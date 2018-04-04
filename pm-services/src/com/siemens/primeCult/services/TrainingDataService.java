package com.siemens.primeCult.services;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import com.siemens.primeCult.core.TrainingDataRecord;
import com.siemens.storage.DBUtil;
import com.siemens.storage.TableRow;

@Path("/modelTraining")
public class TrainingDataService {

	@GET
	@Path("/getAssetsIds")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericEntity<List<Object>> getAllAssetsIDs() {
		String qString = "select * from paramdata";
		return new GenericEntity<List<Object>>(DBUtil.getColumnValues(qString, "assetid")) {
		};
	}

	@GET
	@Path("/getAssetTrainingData/{assetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAll(@PathParam("assetId") String assetId) {
		String qString = "select * from training_data where AssetId='" + assetId + "'";
		List<Object> records = DBUtil.get(qString, new TrainingDataRecord());
		return records;
	}

	@POST
	@Path("/insert")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TrainingDataRecord add(TrainingDataRecord trainingRecord) {
		TableRow row = new TableRow("training_data");
		row.add("AssetId", trainingRecord.getAssetId());
		row.add("Flow", trainingRecord.getxFlow());
		row.add("TDH", trainingRecord.getyHeight());
		row.add("Efficiency", trainingRecord.getyEta());
		int id = DBUtil.insert(row);
		trainingRecord.setId(id);
		boolean isSuccess = id != -1;
		if (isSuccess)
			return trainingRecord;
		else
			return null;
	}

	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String update(TrainingDataRecord trainingRecord) {
		TableRow row = new TableRow("training_data");
		row.add("AssetId", trainingRecord.getAssetId());
		row.add("Flow", trainingRecord.getxFlow());
		row.add("TDH", trainingRecord.getyHeight());
		row.add("Efficiency", trainingRecord.getyEta());
		row.where("id", trainingRecord.getId());
		System.out.println("Id = " + trainingRecord.getId());
		boolean isSuccess = DBUtil.update(row);
		if (isSuccess)
			return "SUCCESS";
		else
			return "FAIL";
	}

	@GET
	@Path("/delete/{recordId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String delete(@PathParam("recordId") String recordId) {
		TableRow row = new TableRow("training_data");
		row.where("id", Integer.valueOf(recordId));
		boolean isSuccess = DBUtil.delete(row);
		System.out.println("Deleted : "+isSuccess+" id "+recordId);
		if (isSuccess)
			return "SUCCESS";
		else
			return "FAIL";
	}
}
