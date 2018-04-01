package com.siemens.primecult.alarmservices;

import static com.siemens.primecult.constants.AlarmStatus.GONE;
import static com.siemens.primecult.constants.AlarmStatus.RAISED;

import java.util.List;
import java.util.Map;

import com.siemens.primecult.constants.AlarmTypes;
import com.siemens.primecult.models.KPIData;

public class KPIAlarmService {

	public static boolean checkKpiStateChange(KPIData calculatedValue, KPIData refrencedValue, float threshold,
			AlarmTypes alarmType, String asset_id, Map<String, List<Integer>> currentAlarmsStatus) {
		int currentState = checkIfKPIAlarm(calculatedValue, refrencedValue, threshold, alarmType) ? RAISED : GONE;
		int previousStae = currentAlarmsStatus.get(asset_id).get(alarmType.getIndex());
		return currentState != previousStae;
	}

	private static boolean checkIfKPIAlarm(KPIData calculatedValue, KPIData refrencedValue, float threshold,
			AlarmTypes alarmType) {
		float refrenceValue = 0;
		float calculateValue = 0;
		switch (alarmType) {
		case TDH:
			refrenceValue = refrencedValue.getTdh();
			calculateValue = calculatedValue.getTdh();
			break;
		case EFFICIENCY:
			refrenceValue = refrencedValue.getEfficiency();
			calculateValue = calculatedValue.getEfficiency();
			break;
		default:
			break;
		}
		float difference = Math.abs(refrenceValue - calculateValue);
		float deviation = (difference / refrenceValue) * 100;
		return deviation >= threshold;
	}
}
