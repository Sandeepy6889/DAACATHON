package com.siemens.primecult.models;

public class ValueRt {

	private String assetID;

	private long timeStamp;

	private double[] values;

	public ValueRt(String assetID, long timeStamp, double[] values) {
		this.setAssetID(assetID);
		this.setTimeStamp(timeStamp);
		this.setValues(values);
	}

	public String getAssetID() {
		return assetID;
	}

	public void setAssetID(String assetID) {
		this.assetID = assetID;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}
}
