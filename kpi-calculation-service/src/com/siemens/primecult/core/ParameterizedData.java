package com.siemens.primecult.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.siemens.storage.DbRowToObject;

@XmlRootElement
public class ParameterizedData implements DbRowToObject {
	private List<Object> pdata = new ArrayList<>();
	private String assetID;
	private String assetName;
	private float ratedPower;
	private float motorEfficiency;
	private float motorRatedSpeed;
	private float minRatedFlowOfPump;
	private float fluidDensity;
	private float threadholdLT;
	private float suctionDiameter;
	private float dischargeDiameter;
	private float eleveationDiff;

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("assetID : ").append(assetID);
		result.append("assetName : ").append(assetName);
		result.append("ratedPower : ").append(ratedPower);
		result.append("motorEfficiency : ").append(motorEfficiency);
		result.append("motorRatedSpeed : ").append(motorRatedSpeed);
		result.append("minRatedFlowOfPump : ").append(minRatedFlowOfPump);
		result.append("fluidDensity : ").append(fluidDensity);
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

	public float getRatedPower() {
		return ratedPower;
	}

	public void setRatedPower(float ratedPower) {
		this.ratedPower = ratedPower;
	}

	public float getMotorEfficiency() {
		return motorEfficiency;
	}

	public void setMotorEfficiency(float motorEfficiency) {
		this.motorEfficiency = motorEfficiency;
	}

	public float getMotorRatedSpeed() {
		return motorRatedSpeed;
	}

	public void setMotorRatedSpeed(float motorRatedSpeed) {
		this.motorRatedSpeed = motorRatedSpeed;
	}

	public float getMinRatedFlowOfPump() {
		return minRatedFlowOfPump;
	}

	public void setMinRatedFlowOfPump(float minRatedFlowOfPump) {
		this.minRatedFlowOfPump = minRatedFlowOfPump;
	}

	public float getFluidDensity() {
		return fluidDensity;
	}

	public void setFluidDensity(float fluidDensity) {
		this.fluidDensity = fluidDensity;
	}

	public float getThreadholdLT() {
		return threadholdLT;
	}

	public void setThreadholdLT(float threadholdLT) {
		this.threadholdLT = threadholdLT;
	}

	public float getSuctionDiameter() {
		return suctionDiameter;
	}

	public void setSuctionDiameter(float suctionDiameter) {
		this.suctionDiameter = suctionDiameter;
	}

	public float getDischargeDiameter() {
		return dischargeDiameter;
	}

	public void setDischargeDiameter(float dischargeDiameter) {
		this.dischargeDiameter = dischargeDiameter;
	}

	public float getEleveationDiff() {
		return eleveationDiff;
	}

	public void setEleveationDiff(float eleveationDiff) {
		this.eleveationDiff = eleveationDiff;
	}

	@Override
	public void fill(ResultSet rs) throws SQLException {
		ParameterizedData param = new ParameterizedData();
		param.setAssetID(rs.getString("assetid"));
		param.setAssetName(rs.getString("assetname"));
		param.setMotorEfficiency(Float.valueOf(rs.getFloat("motorefficiency")));
		param.setRatedPower(Float.valueOf(rs.getFloat("ratedpower")));
		param.setMotorRatedSpeed(Float.valueOf(rs.getFloat("motorratedspeed")));
		param.setMinRatedFlowOfPump(Float.valueOf(rs.getFloat("minratedflowofpump")));
		param.setFluidDensity(Float.valueOf(rs.getFloat("waterdensity")));
		param.setThreadholdLT(Float.valueOf(rs.getFloat("threslt")));
		param.setSuctionDiameter(Float.valueOf(rs.getFloat("suctiondiameter")));
		param.setDischargeDiameter(Float.valueOf(rs.getFloat("dischargediameter")));
		param.setEleveationDiff(Float.valueOf(rs.getFloat("eleveationdiff")));
		pdata.add(param);
	}

	public List<Object> getData() {
		return pdata;
	}

}
