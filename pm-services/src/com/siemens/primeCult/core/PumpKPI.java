package com.siemens.pumpMonitoring.core;

public class PumpKPI {
	private double TDH;
	private double efficiency;
	private double flow;
	private String assetID;
	private int id;
	private long timeStamp;

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getTDH() {
		return TDH;
	}

	public void setTDH(double tDH) {
		TDH = tDH;
	}

	@Override
	public String toString() {
		return "PumpKPI [TDH=" + TDH + ", efficiency=" + efficiency + ", flow=" + flow + ", assetID=" + assetID
				+ ", id=" + id + ", timeStamp=" + timeStamp + "]";
	}

	public double getEfficiency() {
		return efficiency;
	}

	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}

	public double getFlow() {
		return flow;
	}

	public void setFlow(double flow) {
		this.flow = flow;
	}

	public String getAssetID() {
		return assetID;
	}

	public void setAssetID(String assetID) {
		this.assetID = assetID;
	}

	

}
