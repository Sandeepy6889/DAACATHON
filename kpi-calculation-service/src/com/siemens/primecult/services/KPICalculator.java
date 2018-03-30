package com.siemens.primecult.services;

import static com.siemens.primecult.constants.PumpMonitorConstant.DISCH_PRESSURE;
import static com.siemens.primecult.constants.PumpMonitorConstant.FLUID_FLOW_RATE;
import static com.siemens.primecult.constants.PumpMonitorConstant.MOTOR_POWER_INPUT;
import static com.siemens.primecult.constants.PumpMonitorConstant.SUCT_PRESSURE;

import com.siemens.primecult.models.ValueRt;

public class KPICalculator {

	public void calculateKPI(ValueRt requestData) {
		float elevationDiff = 0.0f; // to come from DB
		float suctPipeDiameter = 0.0f; // to come from DB
		float dischPipeDiameter = 0.0f;// to come from DB
		float fluidDensity = 0.0f; // to come from DB
		float motorEfficiency = 0.0f; // to come from DB
		float[] values = requestData.getValues();
		float dischHead = calcDischargeHead(values[DISCH_PRESSURE], fluidDensity, values[FLUID_FLOW_RATE],
				dischPipeDiameter);
		float suctionHead = calcSuctionHead(values[SUCT_PRESSURE], fluidDensity, values[FLUID_FLOW_RATE],
				suctPipeDiameter);
		float tdh = dischHead - suctionHead + elevationDiff;
		float pumpEfficiency = calculateEfficiency(values[DISCH_PRESSURE], values[SUCT_PRESSURE],
				values[FLUID_FLOW_RATE], values[MOTOR_POWER_INPUT], motorEfficiency);
		// get ref tdh
		// get ref eff
		// inser both of them in table

	}

	private float calcDischargeHead(float dischPressure, float fluidDensity, float fluidFlowRate,
			float dischPipeDiameter) {
		return getStaticHead(dischPressure, fluidDensity) + getDynamicHead(fluidFlowRate, dischPipeDiameter);
	}

	private float calcSuctionHead(float suctPressure, float fluidDensity, float fluidFlowRate,
			float suctPipeDiameter) {
		return getStaticHead(suctPressure, fluidDensity) + getDynamicHead(fluidFlowRate, suctPipeDiameter);
	}

	private float getStaticHead(float pressure, float fluidDensity) {

		return (pressure + 14.7f) * 2.31f / fluidDensity;
	}

	private float getDynamicHead(float fluidFlowRate, float pipeDiameter) {

		float velocityInPipe =(float)((fluidFlowRate * 0.321) / (Math.PI * Math.pow((pipeDiameter / 2), 2)));
		return (float)(Math.pow(velocityInPipe, 2) / 64.4);
	}

	private float calculateEfficiency(float discPress, float suctPress, float fluidFlowRate, float motorPowerInput,
			float motorEfficiency) {

		return ((discPress - suctPress) * fluidFlowRate) / (2298 * motorPowerInput * motorEfficiency);
	}

}
