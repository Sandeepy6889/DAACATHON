package com.siemens.primeCult.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ParameterizedData  implements DbRowToObject{
	private List<Object> pdata = new ArrayList<>();
	private String assetID;
	private String assetName;
	private double ratedPower;
	private double motorEfficiency;
	private double motorRatedSpeed;
	private double minRatedFlowOfPump;
	private double waterDensity;
	private double threadholdLT;
	private double suctionDiameter;
	private double dischargeDiameter;
	private double eleveationDiff;

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
		result.append("suctionDiameter : ").append(suctionDiameter);
		result.append("dischargeDiameter : ").append(dischargeDiameter);
		result.append("eleveationDiff : ").append(eleveationDiff);
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

	public double getRatedPower() {
		return ratedPower;
	}

	public void setRatedPower(double ratedPower) {
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

	public double getWaterDensity() {
		return waterDensity;
	}

	public void setWaterDensity(double waterDensity) {
		this.waterDensity = waterDensity;
	}

	public double getThreadholdLT() {
		return threadholdLT;
	}

	public void setThreadholdLT(double threadholdLT) {
		this.threadholdLT = threadholdLT;
	}

	public double getSuctionDiameter() {
		return suctionDiameter;
	}

	public void setSuctionDiameter(double suctionDiameter) {
		this.suctionDiameter = suctionDiameter;
	}

	public double getDischargeDiameter() {
		return dischargeDiameter;
	}

	public void setDischargeDiameter(double dischargeDiameter) {
		this.dischargeDiameter = dischargeDiameter;
	}

	public double getEleveationDiff() {
		return eleveationDiff;
	}

	public void setEleveationDiff(double eleveationDiff) {
		this.eleveationDiff = eleveationDiff;
	}

	@Override
	public void fill(ResultSet rs) throws SQLException {
		ParameterizedData param = new ParameterizedData();
		param.setAssetID(rs.getString("assetid"));
		param.setAssetName(rs.getString("assetname"));
		param.setMotorEfficiency(Double.valueOf(rs.getDouble("motorefficiency")));
		param.setRatedPower(Double.valueOf(rs.getDouble("ratedpower")));
		param.setMotorRatedSpeed(Double.valueOf(rs.getDouble("motorratedspeed")));
		param.setMinRatedFlowOfPump(Double.valueOf(rs.getDouble("minratedflowofpump")));
		param.setWaterDensity(Double.valueOf(rs.getDouble("waterdensity")));
		param.setThreadholdLT(Double.valueOf(rs.getDouble("threslt")));
		param.setSuctionDiameter(Double.valueOf(rs.getDouble("suctiondiameter")));
		param.setDischargeDiameter(Double.valueOf(rs.getDouble("dischargediameter")));
		param.setEleveationDiff(Double.valueOf(rs.getDouble("eleveationdiff")));
		pdata.add(param);
	}

	public List<Object> getData() {
		return pdata;
	}


}
