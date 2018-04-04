package com.siemens.primeCult.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Alarm implements DbRowToObject {

	private String assetId;
	private String alarmType;
	private String alarmStatus;
	private String date;
	private double flow;
	private double suctionPressure;
	private double dischargePressure;
	private double motorInput;
	private String[] alrmStatus = { "Gone", "Raised" };
	private List<Object> alarms = new ArrayList<>();

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(String alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getFlow() {
		return flow;
	}

	public void setFlow(double flow) {
		this.flow = flow;
	}

	public double getSuctionPressure() {
		return suctionPressure;
	}

	public void setSuctionPressure(double suctionPressure) {
		this.suctionPressure = suctionPressure;
	}

	public double getDischargePressure() {
		return dischargePressure;
	}

	public void setDischargePressure(double dischargePressure) {
		this.dischargePressure = dischargePressure;
	}

	public double getMotorInput() {
		return motorInput;
	}

	public void setMotorInput(double motorInput) {
		this.motorInput = motorInput;
	}

	@Override
	public void fill(ResultSet rs) throws SQLException {
		Alarm alarm = new Alarm();
		alarm.setAssetId(rs.getString("asset_id"));
		alarm.setAlarmType(rs.getString("alarm_type"));
		alarm.setAlarmStatus(alrmStatus[rs.getInt("alarm_status")]);
		alarm.setDate(String.valueOf(new Date(rs.getLong("timestamp"))));
		alarm.setFlow(rs.getDouble("fluid_flow"));
		alarm.setSuctionPressure(rs.getDouble("suction_pressure"));
		alarm.setDischargePressure(rs.getDouble("discharge_pressure"));
		alarm.setMotorInput(rs.getDouble("motor_power_input"));
		alarms.add(alarm);
	}

	@Override
	public List<Object> getData() {
		// TODO Auto-generated method stub
		return alarms;
	}

}
