package com.siemens.primeCult.core;

public class PumpReferencedKPI {
	private double refTDH;
	private double refEfficiency;
	private double refFlow;
	private String assetID;
	private int id;
	private long refTimeStamp;
	@Override
	public String toString() {
		return "PumpReferencedKPI [refTDH=" + refTDH + ", refEfficiency=" + refEfficiency + ", refFlow=" + refFlow
				+ ", assetID=" + assetID + ", id=" + id + ", refTimeStamp=" + refTimeStamp + "]";
	}
	public double getRefTDH() {
		return refTDH;
	}
	public void setRefTDH(double refTDH) {
		this.refTDH = refTDH;
	}
	public double getRefEfficiency() {
		return refEfficiency;
	}
	public void setRefEfficiency(double refEfficiency) {
		this.refEfficiency = refEfficiency;
	}
	public double getRefFlow() {
		return refFlow;
	}
	public void setRefFlow(double refFlow) {
		this.refFlow = refFlow;
	}
	public String getAssetID() {
		return assetID;
	}
	public void setAssetID(String assetID) {
		this.assetID = assetID;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getRefTimeStamp() {
		return refTimeStamp;
	}
	public void setRefTimeStamp(long refTimeStamp) {
		this.refTimeStamp = refTimeStamp;
	}
}
