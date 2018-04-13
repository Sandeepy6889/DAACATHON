package com.siemens.primecult.core;

import static com.siemens.primecult.constants.PumpMonitorConstant.DISCH_PRESSURE;
import static com.siemens.primecult.constants.PumpMonitorConstant.FLUID_FLOW_RATE;
import static com.siemens.primecult.constants.PumpMonitorConstant.MOTOR_POWER_INPUT;
import static com.siemens.primecult.constants.PumpMonitorConstant.SUCT_PRESSURE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.primecult.alarmservices.AlarmManagement;
import com.siemens.primecult.alarmservices.FrequencySamplingService;
import com.siemens.primecult.models.KPIData;
import com.siemens.primecult.models.ValueRt;
import com.siemens.storage.InsertOperations;
import com.siemens.storage.SelectOperations;
import com.siemens.storage.TableRow;

public class KPICalculator {
	Connection connection = null;
	InsertOperations inOperation = new InsertOperations();
	private static volatile Map<String, Boolean> currentTrainingStatus = new HashMap<>();

	public KPICalculator(Connection connection) {
		this.connection = connection;
	}
	
	public static String makeEntryToManageAssetTrainingStatus(String assetId) {
		if (currentTrainingStatus.get(assetId) != null)
			return "asset already present";
		currentTrainingStatus.put(assetId, false);
		return "success";
	}
	
	public static String removeEntryOfAssetFromAssetTrainingStatusManagement(String assetId) {
		if (currentTrainingStatus.get(assetId)== null)
			return "asset not configured";
		currentTrainingStatus.remove(assetId);
		return "success";
	}

	public static String changeTrainingStatus(String assetId) {
		if (currentTrainingStatus.get(assetId) == null)
			return "asset not found";
		currentTrainingStatus.put(assetId, true);
		return "success";
	}

	public void calculateKPI(ValueRt requestData) throws SQLException {
		if (AlarmManagement.getCurrentAlarmStatus(requestData.getAssetID()) == null) {
			System.out.println("This asset is not created");
			return;
		}
		
		SelectOperations opr = new SelectOperations();
		String qStr = "select * from paramdata where assetid='" + requestData.getAssetID() + "'";
		ParameterizedData pdata = new ParameterizedData();
		opr.select(connection, qStr, pdata);
		List<Object> assetParams = pdata.getData();
		
		if (assetParams.isEmpty())
			return;
		ParameterizedData paramaData = (ParameterizedData) assetParams.get(0);

		float elevationDiff = paramaData.getEleveationDiff();
		float suctPipeDiameter = paramaData.getSuctionDiameter();
		float dischPipeDiameter = paramaData.getDischargeDiameter();
		float fluidDensity = paramaData.getFluidDensity();
		float motorEfficiency = paramaData.getMotorEfficiency();
		float[] values = requestData.getKpiValues();
		float dischHead = calcDischargeHead(values[DISCH_PRESSURE], fluidDensity, values[FLUID_FLOW_RATE],
				dischPipeDiameter);
		float suctionHead = calcSuctionHead(values[SUCT_PRESSURE], fluidDensity, values[FLUID_FLOW_RATE],
				suctPipeDiameter);
		float tdh = dischHead - suctionHead + elevationDiff;
		float efficiency = calculateEfficiency(values[DISCH_PRESSURE], values[SUCT_PRESSURE], values[FLUID_FLOW_RATE],
				values[MOTOR_POWER_INPUT], motorEfficiency);
		insertKPI("calculated_kpi", tdh, efficiency, requestData);
		Boolean isAssetTrained = currentTrainingStatus.get(requestData.getAssetID());
		float refTDH = 0.0f;
		float refEfficiency = 0.0f;
		if (isAssetTrained) {
			refTDH = getRefValue(requestData.getAssetID(), values[FLUID_FLOW_RATE], "TDH");
			refEfficiency = getRefValue(requestData.getAssetID(), values[FLUID_FLOW_RATE], "Efficiency");
			insertKPI("refrence_kpi", refTDH, refEfficiency, requestData);
		}
		FrequencySamplingService.createFrequencySamples(requestData);
		evaluateAlarmState(tdh, efficiency, refTDH, refEfficiency, requestData, isAssetTrained);
	}

	private void evaluateAlarmState(float tdh, float efficiency, float refTDH, float refEfficiency, ValueRt requestData,
			boolean isAssetTrained) throws SQLException {
		KPIData calculatedKPI = new KPIData(requestData.getKpiValues()[FLUID_FLOW_RATE], efficiency, tdh);
		KPIData refKPI = new KPIData(requestData.getKpiValues()[FLUID_FLOW_RATE], refEfficiency, refTDH);
		new AlarmManagement(connection).setAlarmsState(calculatedKPI, refKPI, requestData, isAssetTrained);
	}

	private void insertKPI(String tableName, float tdh, float efficiency, ValueRt requestData) throws SQLException {
		TableRow row = new TableRow(tableName);
		row.set("AssetId", requestData.getAssetID());
		row.set("Flow", requestData.getKpiValues()[FLUID_FLOW_RATE]);
		row.set("TDH", tdh);
		row.set("Efficiency", efficiency);
		row.set("Timestamp", requestData.getTimeStamp());
		inOperation.insert(connection, row);
	}

	private static float getRefValue(String assetId, float fluidFlowRate, String requestType) {
		System.out.println("Ref Value asset ID -"+ assetId + "fluid flow rate- "+fluidFlowRate + "Request type "+ requestType);
		float refValue = -1.0f;
		try {
			URL url = new URL("http://kpipredictionohio-dev.us-east-2.elasticbeanstalk.com/predict?x="
					+ fluidFlowRate + "&model=" + requestType + "&asset_id=" + assetId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.getResponseMessage();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			refValue = Float.valueOf(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return refValue;
	}

	private float calcDischargeHead(float dischPressure, float fluidDensity, float fluidFlowRate,
			float dischPipeDiameter) {
		return getStaticHead(dischPressure, fluidDensity) + getDynamicHead(fluidFlowRate, dischPipeDiameter);
	}

	private float calcSuctionHead(float suctPressure, float fluidDensity, float fluidFlowRate, float suctPipeDiameter) {
		return getStaticHead(suctPressure, fluidDensity) + getDynamicHead(fluidFlowRate, suctPipeDiameter);
	}

	private float getStaticHead(float pressure, float fluidDensity) {

		return (pressure + 14.7f) * 2.31f / fluidDensity;
	}

	private float getDynamicHead(float fluidFlowRate, float pipeDiameter) {

		float velocityInPipe = (float) ((fluidFlowRate * 0.321) / (Math.PI * Math.pow((pipeDiameter / 2), 2)));
		return (float) (Math.pow(velocityInPipe, 2) / 64.4);
	}

	private float calculateEfficiency(float discPress, float suctPress, float fluidFlowRate, float motorPowerInput,
			float motorEfficiency) {

		if (motorPowerInput <= 0)
			return 0;
		return ((discPress - suctPress) * fluidFlowRate) / (2298 * motorPowerInput * motorEfficiency);
	}

}
