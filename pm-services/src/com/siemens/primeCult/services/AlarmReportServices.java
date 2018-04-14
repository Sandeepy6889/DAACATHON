package com.siemens.primeCult.services;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.primeCult.core.Alarm;
import com.siemens.storage.DBUtil;
import com.siemens.storage.TableRow;

@Path("/alarms")
public class AlarmReportServices {

	@GET
	@Path("/getAlarms/{assetId}/")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAll(@PathParam("assetId") String assetId) {
		String qString = "select * from alarms where Asset_id='" + assetId + "'";
		List<Object> records = DBUtil.get(qString, new Alarm());
		return records;
	}
	
	@GET
	@Path("/getAlarms/{assetId}/{beginTimestamp}/{endTimestamp}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getAlarmsForDuration(@PathParam("assetId") String assetId,@PathParam("beginTimestamp") String beginTimestamp,@PathParam("endTimestamp") String endTimestamp) {
		String qString = "select * from alarms where Asset_id='" + assetId + "' and timestamp >= "+Long.valueOf(beginTimestamp)+" and timestamp <= "+Long.valueOf(endTimestamp)+" order by timestamp asc" ;
		List<Object> records = DBUtil.get(qString, new Alarm());
		return records;
	}
	
	
	public static void main(String[] args) {
		
		for(int i = 20; ;i++) {
			TableRow row = new TableRow("alarms");
			row.add("asset_id", "PUMP01");
			row.add("fluid_flow", i);
			row.add("suction_pressure", i);
			row.add("discharge_pressure", i);
			row.add("motor_power_input", i);
			row.add("alarm_type", "TDH");
			row.add("alarm_status", i%2 == 0 ? 0 :1);
			Date time = new Date();
			row.add("timestamp", time.getTime());
			DBUtil.insert(row);
			System.out.println(i-19+" row inserted "+time);
		}
	}
	
	
}
