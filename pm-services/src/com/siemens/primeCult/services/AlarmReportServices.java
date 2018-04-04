package com.siemens.primeCult.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.primeCult.core.Alarm;
import com.siemens.primeCult.core.TrainingDataRecord;
import com.siemens.storage.DBUtil;

@Path("/alarms")
public class AlarmReportServices {

	@GET
	@Path("/getAlarms/{assetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAll(@PathParam("assetId") String assetId) {
		String qString = "select * from alarms where Asset_id='" + assetId + "'";
		List<Object> records = DBUtil.get(qString, new Alarm());
		return records;
	}
	
	public static void main(String[] args) {
		String qString = "select * from alarms where Asset_id='" + "pump1" + "'";
		List<Object> records = DBUtil.get(qString, new Alarm());
		System.out.println(records);
	}
	
}
