package com.siemens.primecult.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ValueRt {

	private String assetID;

	private long timeStamp;

	private float[] kpiValues;

	private float[] values;
	

	public ValueRt() {
	}

	public ValueRt(String assetID, long timeStamp, float[] kpiValues, float[] values) {
		this.setAssetID(assetID);
		this.setTimeStamp(timeStamp);
		this.setKPIValues(kpiValues);
		this.setVibrationAmplitudes(values);
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

	public float[] getKPIValues() {
		return kpiValues;
	}

	public void setKPIValues(float[] kpiValues) {
		this.kpiValues = kpiValues;
	}

	public float[] getVibrationAmplitudes() {
		return values;
	}

	public void setVibrationAmplitudes(float[] values) {
		this.values = values;
	}
}
