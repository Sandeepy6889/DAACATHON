package com.siemens.primecult.kpiServices;

import static com.siemens.primecult.alarmservices.AlarmManagement.makeEntryToManageAlarmsForAsset;
import static com.siemens.primecult.alarmservices.AlarmManagement.removeEntryOfAssetFromAlarmManagement;

import static com.siemens.primecult.core.KPICalculator.makeEntryToManageAssetTrainingStatus;
import static com.siemens.primecult.core.KPICalculator.removeEntryOfAssetFromAssetTrainingStatusManagement;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.siemens.primecult.alarmservices.AlarmManagement;
import com.siemens.primecult.alarmservices.FrequencySamplingService;
import com.siemens.primecult.core.KPICalculator;
import com.siemens.primecult.models.ValueRt;
import com.siemens.storage.DbConnection;

@Path("/KPI-Calculation")
public class KPICalculationService {

	@POST
	@Path("/calculate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ValueRt post(ValueRt requestData) {
		System.out.println("Request Data "+ requestData);
		Connection connection = null;
		try {
			connection = DbConnection.getDbConnection();
			connection.setAutoCommit(false);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		if (connection == null)
			return null;

		KPICalculator kpiCalculator = new KPICalculator(connection);
		try {
			kpiCalculator.calculateKPI(requestData);
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
			}
			e.printStackTrace();
		} finally {
			DbConnection.releaseResources(connection);
		}
		System.out.println("requestData "+requestData);
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
		makeEntryToManageAlarmsForAsset(assetId);
		return makeEntryToManageAssetTrainingStatus(assetId);
	}
	
	@GET
	@Path("/assetremoved/{assetId}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String clearCache(@PathParam("assetId") String assetId) {
		removeEntryOfAssetFromAlarmManagement(assetId);
		return removeEntryOfAssetFromAssetTrainingStatusManagement(assetId);
	}

	@GET
	@Path("/fftdata/{assetId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public List<List<Float>> getFFTData(@PathParam("assetId") String assetId) {
		return FrequencySamplingService.getFrequencyAmplitudeData(assetId);
	}
}
