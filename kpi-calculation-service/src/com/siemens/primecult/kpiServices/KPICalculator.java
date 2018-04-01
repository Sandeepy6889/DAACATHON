package com.siemens.primecult.kpiServices;

import static com.siemens.primecult.constants.PumpMonitorConstant.DISCH_PRESSURE;
import static com.siemens.primecult.constants.PumpMonitorConstant.FLUID_FLOW_RATE;
import static com.siemens.primecult.constants.PumpMonitorConstant.MOTOR_POWER_INPUT;
import static com.siemens.primecult.constants.PumpMonitorConstant.SUCT_PRESSURE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.siemens.primecult.alarmservices.AlarmManagement;
import com.siemens.primecult.models.KPIData;
import com.siemens.primecult.models.ValueRt;
import com.siemens.storage.DBUtil;
import com.siemens.storage.TableRow;

public class KPICalculator {

	public void calculateKPI(ValueRt requestData) {

		List<Object> assetParams = DBUtil.get(
				"select * from paramdata where assetid='" + requestData.getAssetID() + "'", new ParameterizedData());
		if (assetParams.isEmpty())
			return;
		ParameterizedData paramaData = (ParameterizedData) assetParams.get(0);

		float elevationDiff = paramaData.getEleveationDiff();
		float suctPipeDiameter = paramaData.getSuctionDiameter();
		float dischPipeDiameter = paramaData.getDischargeDiameter();
		float fluidDensity = paramaData.getFluidDensity();
		float motorEfficiency = paramaData.getMotorEfficiency();

		float[] values = requestData.getValues();
		//TODO How to handle the case of fluid density, pipe diameter, motor power input &
		// motor efficiency <=0???
		float dischHead = calcDischargeHead(values[DISCH_PRESSURE], fluidDensity, values[FLUID_FLOW_RATE],
				dischPipeDiameter);
		float suctionHead = calcSuctionHead(values[SUCT_PRESSURE], fluidDensity, values[FLUID_FLOW_RATE],
				suctPipeDiameter);
		float tdh = dischHead - suctionHead + elevationDiff;
		float efficiency = calculateEfficiency(values[DISCH_PRESSURE], values[SUCT_PRESSURE], values[FLUID_FLOW_RATE],
				values[MOTOR_POWER_INPUT], motorEfficiency);
		insertKPI("calculated_kpi", tdh, efficiency, requestData);

		float refTDH = getRefValue(requestData.getAssetID(), values[FLUID_FLOW_RATE], "TDH");
		float refEfficiency = getRefValue(requestData.getAssetID(), values[FLUID_FLOW_RATE], "EFFICIENCY");
		if (refTDH >= 0 || refEfficiency >= 0)//TODO How to handle the case of model is still untrained
			insertKPI("refrence_kpi", refTDH, refEfficiency, requestData);
		//TODO what if model is not trained and we have no ref values
		evaluateAlarmState(tdh, efficiency, refTDH, refEfficiency, requestData);
	}

	private void evaluateAlarmState(float tdh, float efficiency, float refTDH, float refEfficiency, ValueRt requestData) {
		KPIData calculatedKPI = new KPIData(requestData.getValues()[FLUID_FLOW_RATE], efficiency, tdh);
		KPIData refKPI = new KPIData(requestData.getValues()[FLUID_FLOW_RATE], refEfficiency, refTDH);
		new AlarmManagement().setAlarmsState(calculatedKPI, refKPI, requestData);
	}

	private boolean insertKPI(String tableName, float tdh, float efficiency, ValueRt requestData) {
		TableRow row = new TableRow("refrence_kpi");
		row.set("AssetId", requestData.getAssetID());
		row.set("Flow", requestData.getValues()[FLUID_FLOW_RATE]);
		row.set("TDH", tdh);
		row.set("Efficiency", efficiency);
		row.set("Timestmp", requestData.getTimeStamp());
		return DBUtil.insert(row);
	}

	private static float getRefValue(String assetId, float fluidFlowRate, String requestType) {
		float refValue = -1.0f;
		try {
			URL url = new URL("http://kpiprediction.qhk9fghub2.us-west-2.elasticbeanstalk.com/predict?x="
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

		return ((discPress - suctPress) * fluidFlowRate) / (2298 * motorPowerInput * motorEfficiency);
	}

}
