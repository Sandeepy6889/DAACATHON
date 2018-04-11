package com.siemens.primecult.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ValueRt {

	private String assetID;

	private long timeStamp;

	private float[] kpiValues;

	private float[] values;
	
	private float samplingFrequency = 25560.0f;

	public ValueRt() {
	}

	public ValueRt(String assetID, long timeStamp, float[] kpiValues, float[] vibrationAmplitudes , float samplingFrequency) {
		this.setAssetID(assetID);
		this.setTimeStamp(timeStamp);
		this.setKpiValues(kpiValues);
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

	public float[] getVibrationAmplitudes() {
		return values;
	}

	public void setVibrationAmplitudes(float[] values) {
		this.values = values;
	}

	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	public float[] getkpiValues() {
		return kpiValues;
	}

	public void setKpiValues(float[] kpiValues) {
		this.kpiValues = kpiValues;
	}
}
