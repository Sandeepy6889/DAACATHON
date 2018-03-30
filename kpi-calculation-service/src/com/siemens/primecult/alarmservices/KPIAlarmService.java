package com.siemens.primecult.alarmservices;

import java.util.List;
import java.util.Map;

import com.siemens.primecult.constants.AlarmStatus;
import com.siemens.primecult.constants.AlarmTypes;
import com.siemens.primecult.models.KpiData;

public class KPIAlarmService {

	public static boolean checkKpiStateChange(KpiData calculatedValue, KpiData refrencedValue, float threshold,
			int alarmType, String asset_id, Map<String, List<Integer>> currentAlarmsStatus) {
		int currentState = checkIfKPIAlarm(calculatedValue, refrencedValue, threshold , alarmType) ? AlarmStatus.RAISED
				: AlarmStatus.GONE;
		int previousStae = currentAlarmsStatus.get(asset_id).get(alarmType);
		return (currentState != previousStae);
	}

	private static boolean checkIfKPIAlarm(KpiData calculatedValue, KpiData refrencedValue, float threshold,
			int alarmType) {
		float refrenceValue = 0;
		float calculateValue = 0;
		switch (alarmType) {
		case AlarmTypes.TDH:
			refrenceValue = refrencedValue.getTdh();
			calculateValue = calculatedValue.getTdh();
			break;
		case AlarmTypes.EFFICIENCY:
			refrenceValue = refrencedValue.getEfficiency();
			calculateValue = calculatedValue.getEfficiency();
			break;			
		}
		float difference = Math.abs(refrenceValue - calculateValue);
		float deviation = (difference / refrenceValue) * 100;
		return (deviation >= threshold);
	}
}
