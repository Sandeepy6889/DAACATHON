package com.siemens.pumpMonitoring.core;

public class PumpKPI {
	private double TDH;
	private double efficiency;
	private double flow;
	private String assetID;
	private int id;

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("id : ").append(id).append(", assetID : ").append(assetID);
		result.append("TDH : ").append(TDH).append(", efficiency : ").append(efficiency);
		result.append(", flow : ").append(flow).append(", assetID : ").append(assetID);
		return result.toString();
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
