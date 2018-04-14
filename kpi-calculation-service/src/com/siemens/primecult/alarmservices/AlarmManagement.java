package com.siemens.primecult.alarmservices;

import static com.siemens.primecult.alarmservices.KPIAlarmService.checkKpiStateChange;
import static com.siemens.primecult.alarmservices.PreventiveAlarmService.checkPreventiveAlarmStateChange;
import static com.siemens.primecult.alarmservices.VibrationAlarmService.checkVibrationAlarmStateChange;
import static com.siemens.primecult.constants.AlarmStatus.GONE;
import static com.siemens.primecult.constants.AlarmStatus.NOT_DEFINED;
import static com.siemens.primecult.constants.AlarmStatus.RAISED;
import static com.siemens.primecult.constants.AlarmTypes.BLOCKAGE;
import static com.siemens.primecult.constants.AlarmTypes.DRYRUN;
import static com.siemens.primecult.constants.AlarmTypes.EFFICIENCY;
import static com.siemens.primecult.constants.AlarmTypes.IMPELLER_WEAR_COMBINATION;
import static com.siemens.primecult.constants.AlarmTypes.TDH;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.primecult.constants.AlarmTypes;
import com.siemens.primecult.models.KPIData;
import com.siemens.primecult.models.ValueRt;
import com.siemens.storage.DBUtil;
import com.siemens.storage.InsertOperations;
import com.siemens.storage.Pair;
import com.siemens.storage.TableRow;

public class AlarmManagement {

	private static volatile Map<String, List<Integer>> currentAlarmsStatus = new HashMap<>();

	private Connection connection;
	InsertOperations inOperation = new InsertOperations();

	public AlarmManagement(Connection connection) {
		this.connection = connection;
	}

	public static List<Integer> getCurrentAlarmStatus(String assetId) {
		return currentAlarmsStatus.get(assetId);
	}

	public static String makeEntryToManageAlarmsForAsset(String assetId) {
		if (currentAlarmsStatus.get(assetId) != null)
			return "asset already present";
		currentAlarmsStatus.put(assetId, Arrays.asList(GONE, GONE, GONE, GONE, GONE));
		return "success";
	}

	public static String removeEntryOfAssetFromAlarmManagement(String assetId) {
		if (currentAlarmsStatus.get(assetId) == null)
			return "asset not configured";
		currentAlarmsStatus.remove(assetId);
		return "success";
	}

	public void setAlarmsState(KPIData calculatedKPI, KPIData refrencedKPI, ValueRt rawValue, boolean isAssetTrained)
			throws SQLException {

		// get threshold from db
		float threshold = fetchThresholdFromDb(rawValue.getAssetID());
		// check for TDH alarm
		if (isAssetTrained && checkKpiStateChange(calculatedKPI, refrencedKPI, threshold, TDH, rawValue.getAssetID(),
				currentAlarmsStatus))
			changeStateInCacheAndDb(TDH, rawValue);
		else if (!isAssetTrained)
			currentAlarmsStatus.get(rawValue.getAssetID()).set(TDH.getIndex(), NOT_DEFINED);

		// check for efficiency alarm
		if (isAssetTrained && checkKpiStateChange(calculatedKPI, refrencedKPI, threshold, EFFICIENCY,
				rawValue.getAssetID(), currentAlarmsStatus))
			changeStateInCacheAndDb(EFFICIENCY, rawValue);
		else if (!isAssetTrained)
			currentAlarmsStatus.get(rawValue.getAssetID()).set(EFFICIENCY.getIndex(), NOT_DEFINED);

		// check for blockage alarm
		if (checkPreventiveAlarmStateChange(BLOCKAGE, rawValue, currentAlarmsStatus)) {
			System.out.println("checking to send mail blockage");
			if (RAISED == changeStateInCacheAndDb(BLOCKAGE, rawValue)) {
				System.out.println("sending mail blckage");
				sendMailForAlarm(rawValue.getAssetID(), "BLOCKAGE");
			}
		}

		// check for dryrun alarm
		if (checkPreventiveAlarmStateChange(DRYRUN, rawValue, currentAlarmsStatus)) {
			System.out.println("checking to send mail dry");
			if (RAISED == changeStateInCacheAndDb(DRYRUN, rawValue)) {
				System.out.println("sending mail dry");
				sendMailForAlarm(rawValue.getAssetID(), "DRYRUN");
			}
		}
		// check for Impeller wear combination alarm
		if (checkVibrationAlarmStateChange(IMPELLER_WEAR_COMBINATION, rawValue, currentAlarmsStatus))
			changeStateInCacheAndDb(IMPELLER_WEAR_COMBINATION, rawValue);
	}
	
	

	private float fetchThresholdFromDb(String assetId) {
		List<Object> values = DBUtil.getColumnValues("select threslt from paramdata where assetid='" + assetId + "'",
				"threslt");
		return values.size() == 0 ? 0 : (Float) ((Pair) values.get(0)).getValue();
	}

	private int changeStateInCacheAndDb(AlarmTypes alarmType, ValueRt rawValues) throws SQLException {
		String assetId = rawValues.getAssetID();
		int stateInCache = currentAlarmsStatus.get(assetId).get(alarmType.getIndex());
		int newState = -1;
		switch (stateInCache) {
		case RAISED:
			newState = GONE;
			break;
		case GONE:
			newState = RAISED;
			break;
		default:
			newState = GONE;
		}
		currentAlarmsStatus.get(assetId).set(alarmType.getIndex(), newState);
		float[] measuredValues = rawValues.getKpiValues();
		TableRow row = new TableRow("alarms");
		row.set("asset_id", assetId);
		row.set("fluid_flow", measuredValues[FLUID_FLOW_RATE]);
		row.set("suction_pressure", measuredValues[SUCT_PRESSURE]);
		row.set("discharge_pressure", measuredValues[DISCH_PRESSURE]);
		row.set("motor_power_input", measuredValues[MOTOR_POWER_INPUT]);
		row.set("alarm_type", alarmType.getValue());
		row.set("alarm_status", currentAlarmsStatus.get(assetId).get(alarmType.getIndex()));
		row.set("timestamp", rawValues.getTimeStamp());
		inOperation.insert(connection, row);
		return newState;
	}

	private static void sendMailForAlarm(String assetId, String raisedAlarm) {
		try {
			List<Object> columnValues = DBUtil.getColumnValues("select alarm_subs from urls_info", "alarm_subs");
			String eMailSubURL = ((Pair) columnValues.get(0)).getValue().toString();
			URL url = new URL(eMailSubURL + "/publishMail/" + assetId + "/" + raisedAlarm);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			String responseMessage = conn.getResponseMessage();
			System.out.println(responseMessage);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return refValue;
	}
	
	public static void main(String[] args) {
		sendMailForAlarm("PUMP03", "DRYRUN");
	}

}
