package com.siemens.primecult.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ValueRt {

	private String assetID;

	private long timeStamp;

	private float[] values;

	private float[] vibrationAmplitudes;
	
	private float samplingFrequency = 25560.0f;

	public ValueRt() {
	}

	public ValueRt(String assetID, long timeStamp, float[] values, float[] vibrationAmplitudes , float samplingFrequency) {
		this.setAssetID(assetID);
		this.setTimeStamp(timeStamp);
		this.setValues(values);
		this.setVibrationAmplitudes(vibrationAmplitudes);
		this.setSamplingFrequency(samplingFrequency);
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

	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}
}
