package com.siemens.primecult.alarmservices;

import static com.siemens.primecult.constants.AlarmStatus.GONE;
import static com.siemens.primecult.constants.AlarmStatus.RAISED;
import static com.siemens.primecult.constants.PumpMonitorConstant.DISCH_PRESSURE;
import static com.siemens.primecult.constants.PumpMonitorConstant.FLUID_FLOW_RATE;
import static com.siemens.primecult.constants.PumpMonitorConstant.SUCT_PRESSURE;
import static com.siemens.primecult.utils.ArithmeticUtil.isFloatValueEqual;

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
		float[] measuredParameters = rawValues.getKpiValues();
		switch (alarmType) {
		case BLOCKAGE:
			return isFloatValueEqual(measuredParameters[FLUID_FLOW_RATE], 0.0f)
					&& (!isFloatValueEqual(measuredParameters[SUCT_PRESSURE], 0.0f))
					&& (!isFloatValueEqual(measuredParameters[DISCH_PRESSURE], 0.0f));
		case DRYRUN:
			return isFloatValueEqual(measuredParameters[FLUID_FLOW_RATE], 0.0f)
					&& isFloatValueEqual(measuredParameters[SUCT_PRESSURE], 0.0f)
					&& isFloatValueEqual(measuredParameters[DISCH_PRESSURE], 0.0f);
		default:
			return false;
		}
	}

}
