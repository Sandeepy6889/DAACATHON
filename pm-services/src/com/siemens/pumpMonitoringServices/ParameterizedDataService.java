package com.siemens.pumpMonitoringServices;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.pumpMonitoring.core.ParameterizedData;
import com.siemens.pumpMonitoring.core.ParameterizedDataResultSet;
import com.siemens.storage.DbConnection;
import com.siemens.storage.SelectOperations;

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
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ParameterizedData> getAll() {
		ParameterizedDataResultSet obj = null;
		Connection connection = null;
		try {
			connection = DbConnection.getDbConnection();
			obj = new ParameterizedDataResultSet();
			String qStr = "SELECT * from paramdata";
			SelectOperations opr = new SelectOperations();
			opr.select(connection, qStr, obj);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbConnection.releaseResources(connection);
		}
		return data = obj.getData();
	}

	@POST
	@Path("/insert")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ParameterizedData add(ParameterizedData pumpData) {
		data.add(pumpData);
		return pumpData;
	}

}
