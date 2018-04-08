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
		records.add(info);
	}
	@Override
	public List<Object> getData() {
		return records;
	}
	
	
}
