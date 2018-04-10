package com.siemens.primecult.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ValueRt {

	private String assetID;

	private long timeStamp;

	private float[] values;

	private float[] vibrationAmplitudes;
	

	public ValueRt() {
	}

	public ValueRt(String assetID, long timeStamp, float[] values, float[] vibrationAmplitudes) {
		this.setAssetID(assetID);
		this.setTimeStamp(timeStamp);
		this.setValues(values);
		this.setVibrationAmplitudes(vibrationAmplitudes);
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

	public float[] getValues() {
		return values;
	}

	public void setValues(float[] values) {
		this.values = values;
	}

	public float[] getVibrationAmplitudes() {
		return vibrationAmplitudes;
	}

	public void setVibrationAmplitudes(float[] vibrationAmplitudes) {
		this.vibrationAmplitudes = vibrationAmplitudes;
	}
}
