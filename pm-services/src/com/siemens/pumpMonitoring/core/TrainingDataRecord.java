package com.siemens.pumpMonitoring.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrainingDataRecord implements DbRowToObject{
	private List<Object> records = new ArrayList<>();
	private double xFlow;
	private double yHeight;
	private double yEta;
	private int id;
	private String assetId;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("id : ").append(id).append(", assetId : ").append(assetId);
		builder.append(", xFlow : ").append(xFlow).append(", yHeight").append(yHeight).append(", yEta").append(yEta);
		return builder.toString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public double getxFlow() {
		return xFlow;
	}

	public void setxFlow(double xFlow) {
		this.xFlow = xFlow;
	}

	public double getyHeight() {
		return yHeight;
	}

	public void setyHeight(double yHeight) {
		this.yHeight = yHeight;
	}

	public double getyEta() {
		return yEta;
	}

	public void setyEta(double yEta) {
		this.yEta = yEta;
	}

	@Override
	public void fill(ResultSet rs) throws SQLException {
		TrainingDataRecord record = new TrainingDataRecord();
		record.setAssetId(rs.getString("AssetId"));
		record.setId(rs.getInt("id"));
		record.setxFlow(rs.getDouble("Flow"));
		record.setyHeight(rs.getDouble("TDH"));
		record.setyEta(rs.getDouble("Efficiency"));
		records.add(record);
	}

	@Override
	public List<Object> getData() {
		return records;
	}

}
