package com.siemens.primecult.alarmservices;

import static com.siemens.primecult.constants.AlarmStatus.GONE;
import static com.siemens.primecult.constants.AlarmStatus.RAISED;
import static com.siemens.primecult.constants.PumpMonitorConstant.DISCH_PRESSURE;
import static com.siemens.primecult.constants.PumpMonitorConstant.FLUID_FLOW_RATE;
import static com.siemens.primecult.constants.PumpMonitorConstant.SUCT_PRESSURE;

import java.util.List;
import java.util.Map;

import com.siemens.primecult.constants.AlarmTypes;
import com.siemens.primecult.models.ValueRt;

public class PreventiveAlarmService {

	public static boolean checkPreventiveAlarmStateChange(AlarmTypes alarmType, ValueRt rawValues,
			Map<String, List<Integer>> currentAlarmsStatus) {
		int currentState = checkIfPreventiveAlarm(alarmType, rawValues) ? RAISED : GONE;
		int previousStae = currentAlarmsStatus.get(rawValues.getAssetID()).get(alarmType.getIndex());
		return (currentState != previousStae);
	}

	private static boolean checkIfPreventiveAlarm(AlarmTypes alarmType, ValueRt rawValues) {
		float[] measuredParameters = rawValues.getkpiValues();
		switch (alarmType) {
		case BLOCKAGE:
			return measuredParameters[FLUID_FLOW_RATE] == 0 && measuredParameters[SUCT_PRESSURE] != 0
					&& measuredParameters[DISCH_PRESSURE] != 0;
		case DRYRUN:
			return measuredParameters[FLUID_FLOW_RATE] == 0 && measuredParameters[SUCT_PRESSURE] == 0
					&& measuredParameters[DISCH_PRESSURE] == 0;
		default:
			return false;
		}
	}

}
