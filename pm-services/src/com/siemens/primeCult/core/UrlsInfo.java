package com.siemens.primeCult.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UrlsInfo implements DbRowToObject{

	private int id;
	private String assetCreated;
	private String assetTrained;
	private String alarmsStatus;
	private String teachModel;
	private String assetCreatedPClient;
	private String enggAssets;
	private String vibration;
	private String assetRemoved;
	private String clearAlarmCache;
	private String alarmSubs;
	private String email;
	private String topicArn;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTopicArn() {
		return topicArn;
	}
	public void setTopicArn(String topicArn) {
		this.topicArn = topicArn;
	}
	public String getAlarmSubs() {
		return alarmSubs;
	}
	public void setAlarmSubs(String alarmSubs) {
		this.alarmSubs = alarmSubs;
	}
	public String getAssetRemoved() {
		return assetRemoved;
	}
	public void setAssetRemoved(String assetRemoved) {
		this.assetRemoved = assetRemoved;
	}
	public String getClearAlarmCache() {
		return clearAlarmCache;
	}
	public void setClearAlarmCache(String clearAlarmCache) {
		this.clearAlarmCache = clearAlarmCache;
	}
	public String getVibration() {
		return vibration;
	}
	public void setVibration(String vibration) {
		this.vibration = vibration;
	}
	public String getEnggAssets() {
		return enggAssets;
	}
	public void setEnggAssets(String enggAssets) {
		this.enggAssets = enggAssets;
	}
	public String getAssetCreatedPClient() {
		return assetCreatedPClient;
	}
	public void setAssetCreatedPClient(String assetCreatedPClient) {
		this.assetCreatedPClient = assetCreatedPClient;
	}
	public String getAssetCreated() {
		return assetCreated;
	}
	public void setAssetCreated(String assetCreated) {
		this.assetCreated = assetCreated;
	}
	public String getAssetTrained() {
		return assetTrained;
	}
	public void setAssetTrained(String assetTrained) {
		this.assetTrained = assetTrained;
	}
	public String getAlarmsStatus() {
		return alarmsStatus;
	}
	public void setAlarmsStatus(String alarmsStatus) {
		this.alarmsStatus = alarmsStatus;
	}
	public String getTeachModel() {
		return teachModel;
	}
	public void setTeachModel(String teachModel) {
		this.teachModel = teachModel;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Object> getRecords() {
		return records;
	}
	public void setRecords(List<Object> records) {
		this.records = records;
	}

	private List<Object> records = new ArrayList<>();
	@Override
	public void fill(ResultSet rs) throws SQLException {
		UrlsInfo info = new UrlsInfo();
		info.setId(rs.getInt("id"));
		info.setAssetCreated(rs.getString("asset_created"));
		info.setAssetTrained(rs.getString("asset_trained"));
		info.setAlarmsStatus(rs.getString("alarms_status"));
		info.setTeachModel(rs.getString("teach_model"));
		info.setAssetCreatedPClient(rs.getString("asset_created_p_client"));
		info.setEnggAssets(rs.getString("engg_assets"));
		info.setVibration(rs.getString("vibration"));
		info.setAssetRemoved(rs.getString("asset_removed"));
		info.setClearAlarmCache(rs.getString("clear_alarm_cache"));
		info.setAlarmSubs(rs.getString("alarm_subs"));
		System.out.println("From database "+rs.getString("alarm_subs"));
		info.setEmail(rs.getString("email"));
		info.setTopicArn(rs.getString("topic_arn"));
		records.add(info);
	}
	@Override
	public List<Object> getData() {
		return records;
	}
	
	
}
