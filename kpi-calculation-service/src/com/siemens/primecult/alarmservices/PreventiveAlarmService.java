package com.siemens.primecult.alarmservices;

import java.util.List;
import java.util.Map;

import com.siemens.primecult.constants.AlarmStatus;
import com.siemens.primecult.constants.AlarmTypes;
import com.siemens.primecult.constants.PumpMonitorConstant;
import com.siemens.primecult.models.ValueRt;

public class PreventiveAlarmService {

	public static boolean checkPreventiveAlarmStateChange(AlarmTypes alarmType, String asset_id, ValueRt rawValues,
			Map<String, List<Integer>> currentAlarmsStatus) {
		int currentState = checkIfPreventiveAlarm(alarmType, rawValues) ? AlarmStatus.RAISED : AlarmStatus.GONE;
		int previousStae = currentAlarmsStatus.get(asset_id).get(alarmType.getIndex());
		return (currentState != previousStae);
	}

	private static boolean checkIfPreventiveAlarm(AlarmTypes alarmType, ValueRt rawValues) {
		float[] measuredParameters = rawValues.getValues();
		switch (alarmType) {
		case BLOCKAGE:
			return (measuredParameters[PumpMonitorConstant.FLUID_FLOW_RATE] == 0
					&& measuredParameters[PumpMonitorConstant.SUCT_PRESSURE] != 0
					&& measuredParameters[PumpMonitorConstant.DISCH_PRESSURE] != 0);
		case DRYRUN:
			return (measuredParameters[PumpMonitorConstant.FLUID_FLOW_RATE] == 0
					&& measuredParameters[PumpMonitorConstant.SUCT_PRESSURE] == 0
					&& measuredParameters[PumpMonitorConstant.DISCH_PRESSURE] == 0);
		default:
			return false;
		}
	}

}
