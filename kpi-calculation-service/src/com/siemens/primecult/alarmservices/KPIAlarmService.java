package com.siemens.primecult.alarmservices;

import java.util.List;
import java.util.Map;

import com.siemens.primecult.constants.AlarmStatus;

public class KPIAlarmService {

	public static boolean checkKpiStateChange(float calculatedValue, float refrencedValue, float threshold,
			int alarmType, String asset_id, Map<String, List<Integer>> currentAlarmsStatus) {
		int currentState = checkIfKPIAlarm(calculatedValue, refrencedValue, threshold) ? AlarmStatus.RAISED
				: AlarmStatus.GONE;
		int previousStae = currentAlarmsStatus.get(asset_id).get(alarmType);
		return (currentState != previousStae);
	}

	private static boolean checkIfKPIAlarm(float calculatedValue, float refrencedValue, float threshold) {
		float difference = Math.abs(refrencedValue - calculatedValue);
		float deviation = (difference / refrencedValue) * 100;
		return (deviation >= threshold);
	}
}
