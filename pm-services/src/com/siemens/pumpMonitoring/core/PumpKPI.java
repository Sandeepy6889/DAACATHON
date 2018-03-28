package com.siemens.pumpMonitoring.core;

public class PumpKPI {
private double TDH;
private double efficiency;
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
public int getAssetID() {
	return assetID;
}
public void setAssetID(int assetID) {
	this.assetID = assetID;
}
private double flow;
private int assetID;

}
