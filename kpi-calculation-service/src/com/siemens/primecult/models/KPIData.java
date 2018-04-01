package com.siemens.primecult.models;

public class KPIData {
	
	private float flow;
	private float efficiency;
	private float tdh;
	
	public KPIData(float flow, float efficiency, float tdh) {
		super();
		this.flow = flow;
		this.efficiency = efficiency;
		this.tdh = tdh;
	}
		
	public float getFlow() {
		return flow;
	}
	public void setFlow(float flow) {
		this.flow = flow;
	}
	public float getEfficiency() {
		return efficiency;
	}
	public void setEfficiency(float efficiency) {
		this.efficiency = efficiency;
	}
	public float getTdh() {
		return tdh;
	}
	public void setTdh(float tdh) {
		this.tdh = tdh;
	}
}
