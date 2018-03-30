package com.siemens.primecult.alarmservices;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.primecult.constants.AlarmStatus;
import com.siemens.primecult.constants.AlarmTypes;
import com.siemens.primecult.constants.PumpMonitorConstant;
import com.siemens.primecult.models.KpiData;
import com.siemens.primecult.models.ValueRt;
import com.siemens.storage.DBUtil;
import com.siemens.storage.Pair;
import com.siemens.storage.TableRow;

public class AlarmManagement {

	private static Map<String, List<Integer>> currentAlarmsStatus = new HashMap<>();
	static {
		makeEntryToManageAlarmsForAsset("1");
	}

	public static void makeEntryToManageAlarmsForAsset(String asset_id) {
		currentAlarmsStatus.put(asset_id,
				Arrays.asList(AlarmStatus.GONE, AlarmStatus.GONE, AlarmStatus.GONE, AlarmStatus.GONE));
	}

	public void setAlarmsState(KpiData calculatedKPI, KpiData refrencedKPI, ValueRt rawValues) {
		// get threshold from db
		float threshold = fetchThresholdFromDb(rawValues.getAssetID());
		// check for TDH alarm
		if (KPIAlarmService.checkKpiStateChange(calculatedKPI, refrencedKPI, threshold, AlarmTypes.TDH,
				rawValues.getAssetID(), currentAlarmsStatus))
			changeStateInCacheAndDb(AlarmTypes.TDH, rawValues);

		// check for efficiency alarm
		if (KPIAlarmService.checkKpiStateChange(calculatedKPI, refrencedKPI, threshold, AlarmTypes.EFFICIENCY,
				rawValues.getAssetID(), currentAlarmsStatus))
			changeStateInCacheAndDb(AlarmTypes.EFFICIENCY, rawValues);

		// check for blockage alarm
		if (PreventiveAlarmService.checkPreventiveAlarmStateChange(AlarmTypes.BLOCKAGE, rawValues, currentAlarmsStatus))
			changeStateInCacheAndDb(AlarmTypes.BLOCKAGE, rawValues);

		// check for dryrun alarm
		if (PreventiveAlarmService.checkPreventiveAlarmStateChange(AlarmTypes.DRYRUN, rawValues, currentAlarmsStatus))
			changeStateInCacheAndDb(AlarmTypes.DRYRUN, rawValues);
	}

	private float fetchThresholdFromDb(String assetId) {
		List<Object> values = DBUtil.getColumnValues("select threslt from paramdata where assetid='" + assetId + "'",
				"threslt");
		return values.size() == 0 ? 0 : (Float) ((Pair) values.get(0)).getValue();
	}

	private void changeStateInCacheAndDb(AlarmTypes alarmType, ValueRt rawValues) {
		int stateInCache = currentAlarmsStatus.get(rawValues.getAssetID()).get(alarmType.getIndex());
		String asset_id = rawValues.getAssetID();
		switch (stateInCache) {
		case AlarmStatus.RAISED:
			currentAlarmsStatus.get(asset_id).set(alarmType.getIndex(), AlarmStatus.GONE);
			break;
		case AlarmStatus.GONE:
			currentAlarmsStatus.get(asset_id).set(alarmType.getIndex(), AlarmStatus.RAISED);
			break;
		default:
			currentAlarmsStatus.get(asset_id).set(alarmType.getIndex(), AlarmStatus.GONE);
		}

		float[] measuredValues = rawValues.getValues();
		TableRow row = new TableRow("alarms");
		row.set("asset_id", asset_id);
		row.set("fluid_flow", measuredValues[PumpMonitorConstant.FLUID_FLOW_RATE]);
		row.set("suction_pressure", measuredValues[PumpMonitorConstant.SUCT_PRESSURE]);
		row.set("discharge_pressure", measuredValues[PumpMonitorConstant.DISCH_PRESSURE]);
		row.set("motor_power_input", measuredValues[PumpMonitorConstant.MOTOR_POWER_INPUT]);
		row.set("alarm_type", alarmType.getValue());
		row.set("alarm_status", currentAlarmsStatus.get(asset_id).get(alarmType.getIndex()));
		row.set("timestamp", rawValues.getTimeStamp());
		if (DBUtil.insert(row)) {
			System.out.println("Inserted successfully");
		} else
			System.out.println("Insertion Unsuccesssfull");
	}

}
