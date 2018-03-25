package com.siemens.pumpMonitoring.core;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ParameterizedData {
	private String assetID;
	private String assetName;
	private int ratedPower;
	private double motorEfficiency;
	private double motorRatedSpeed;
	private double minRatedFlowOfPump;
	private int waterDensity;
	private double threadholdLT;

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("assetID : ").append(assetID);
		result.append("assetName : ").append(assetName);
		result.append("ratedPower : ").append(ratedPower);
		result.append("motorEfficiency : ").append(motorEfficiency);
		result.append("motorRatedSpeed : ").append(motorRatedSpeed);
		result.append("minRatedFlowOfPump : ").append(minRatedFlowOfPump);
		result.append("waterDensity : ").append(waterDensity);
		result.append("threadholdLT : ").append(threadholdLT);
		
		return result.toString();
	}
	
	public String getAssetID() {
		return assetID;
	}

	public void setAssetID(String assetID) {
		this.assetID = assetID;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public int getRatedPower() {
		return ratedPower;
	}

	public void setRatedPower(int ratedPower) {
		this.ratedPower = ratedPower;
	}

	public double getMotorEfficiency() {
		return motorEfficiency;
	}

	public void setMotorEfficiency(double motorEfficiency) {
		this.motorEfficiency = motorEfficiency;
	}

	public double getMotorRatedSpeed() {
		return motorRatedSpeed;
	}

	public void setMotorRatedSpeed(double motorRatedSpeed) {
		this.motorRatedSpeed = motorRatedSpeed;
	}

	public double getMinRatedFlowOfPump() {
		return minRatedFlowOfPump;
	}

	public void setMinRatedFlowOfPump(double minRatedFlowOfPump) {
		this.minRatedFlowOfPump = minRatedFlowOfPump;
	}

	public int getWaterDensity() {
		return waterDensity;
	}

	public void setWaterDensity(int waterDensity) {
		this.waterDensity = waterDensity;
	}

	public double getThreadholdLT() {
		return threadholdLT;
	}

	public void setThreadholdLT(double threadholdLT) {
		this.threadholdLT = threadholdLT;
	}

}
