package com.siemens.primecult.alarmservices;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.primecult.constants.AlarmStatus;
import com.siemens.primecult.constants.AlarmTypes;
import com.siemens.primecult.models.KpiData;
import com.siemens.primecult.models.ValueRt;

public class AlarmManagement {

	private static Map<String, List<Integer>> currentAlarmsStatus = new HashMap<>();
	static {
		makeEntryToManageAlarmsForAsset("1");
	}

	public static void makeEntryToManageAlarmsForAsset(String asset_id) {
		currentAlarmsStatus.put(asset_id,
				Arrays.asList(AlarmStatus.GONE, AlarmStatus.GONE, AlarmStatus.GONE, AlarmStatus.GONE));
	}

	public void setAlarmsState(KpiData calculatedKPI, KpiData refrencedKPI, String asset_id, ValueRt rawValues) {
		// get threshold from db
		float threshold = fetchThresholdFromDb(asset_id);
		// check for TDH alarm
		if (KPIAlarmService.checkKpiStateChange(calculatedKPI, refrencedKPI, threshold, AlarmTypes.TDH, asset_id,
				currentAlarmsStatus))
			changeStateInCacheAndDb(asset_id, AlarmTypes.TDH, rawValues);

		// check for efficiency alarm
		if (KPIAlarmService.checkKpiStateChange(calculatedKPI, refrencedKPI, threshold, AlarmTypes.EFFICIENCY, asset_id,
				currentAlarmsStatus))
			changeStateInCacheAndDb(asset_id, AlarmTypes.EFFICIENCY, rawValues);

		// check for blockage alarm
		if (PreventiveAlarmService.checkPreventiveAlarmStateChange(AlarmTypes.BLOCKAGE, asset_id, rawValues,
				currentAlarmsStatus))
			changeStateInCacheAndDb(asset_id, AlarmTypes.BLOCKAGE, rawValues);

		// check for dryrun alarm
		if (PreventiveAlarmService.checkPreventiveAlarmStateChange(AlarmTypes.DRYRUN, asset_id, rawValues,
				currentAlarmsStatus))
			changeStateInCacheAndDb(asset_id, AlarmTypes.DRYRUN, rawValues);
	}

	private float fetchThresholdFromDb(String assetId) {

		return 10;
	}

	private void changeStateInCacheAndDb(String asset_id, int alarmType, ValueRt rawValues) {
		int stateInCache = currentAlarmsStatus.get(asset_id).get(alarmType);
		switch (stateInCache) {
		case AlarmStatus.RAISED:
			currentAlarmsStatus.get(asset_id).set(alarmType, AlarmStatus.GONE);
			break;
		case AlarmStatus.GONE:
			currentAlarmsStatus.get(asset_id).set(alarmType, AlarmStatus.RAISED);
			break;
		default:
			currentAlarmsStatus.get(asset_id).set(alarmType, AlarmStatus.GONE);
		}
	}

}
