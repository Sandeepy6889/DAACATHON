package com.siemens.primecult.services;

import static com.siemens.primecult.utils.PumpMonitorConstant.DISCH_PRESSURE;
import static com.siemens.primecult.utils.PumpMonitorConstant.FLUID_FLOW_RATE;
import static com.siemens.primecult.utils.PumpMonitorConstant.MOTOR_POWER_INPUT;
import static com.siemens.primecult.utils.PumpMonitorConstant.SUCT_PRESSURE;

import com.siemens.primecult.models.ValueRt;

public class KPICalculator {

	public void calculateKPI(ValueRt requestData) {
		double elevationDiff = 0.0; // to come from DB
		double suctPipeDiameter = 0.0; // to come from DB
		double dischPipeDiameter = 0.0;// to come from DB
		double fluidDensity = 0.0; // to come from DB
		double motorEfficiency = 0.0; // to come from DB
		double[] values = requestData.getValues();
		double dischHead = calcDischargeHead(values[DISCH_PRESSURE], fluidDensity, values[FLUID_FLOW_RATE],
				dischPipeDiameter);
		double suctionHead = calcSuctionHead(values[SUCT_PRESSURE], fluidDensity, values[FLUID_FLOW_RATE],
				suctPipeDiameter);
		double tdh = dischHead - suctionHead + elevationDiff;
		double pumpEfficiency = calculateEfficiency(values[DISCH_PRESSURE], values[SUCT_PRESSURE],
				values[FLUID_FLOW_RATE], values[MOTOR_POWER_INPUT], motorEfficiency);
		// get ref tdh
		// get ref eff
		// inser both of them in table

	}

	private double calcDischargeHead(double dischPressure, double fluidDensity, double fluidFlowRate,
			double dischPipeDiameter) {
		return getStaticHead(dischPressure, fluidDensity) + getDynamicHead(fluidFlowRate, dischPipeDiameter);
	}

	private double calcSuctionHead(double suctPressure, double fluidDensity, double fluidFlowRate,
			double suctPipeDiameter) {
		return getStaticHead(suctPressure, fluidDensity) + getDynamicHead(fluidFlowRate, suctPipeDiameter);
	}

	private double getStaticHead(double pressure, double fluidDensity) {

		return (pressure + 14.7) * 2.31 / fluidDensity;
	}

	private double getDynamicHead(double fluidFlowRate, double pipeDiameter) {

		double velocityInPipe = (fluidFlowRate * 0.321) / (Math.PI * Math.pow((pipeDiameter / 2), 2));
		return Math.pow(velocityInPipe, 2) / 64.4;
	}

	private double calculateEfficiency(double discPress, double suctPress, double fluidFlowRate, double motorPowerInput,
			double motorEfficiency) {

		return ((discPress - suctPress) * fluidFlowRate) / (2298 * motorPowerInput * motorEfficiency);
	}

}
