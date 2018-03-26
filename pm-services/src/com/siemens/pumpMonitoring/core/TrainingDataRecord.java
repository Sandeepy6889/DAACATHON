package com.siemens.pumpMonitoring.core;

public class TrainingDataRecord {
	private double xFlow;
	private double yHeight;
	private double yEta;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("xFlow : ").append(xFlow).append(", yHeight").append(yHeight).append(", yEta").append(yEta);
		return builder.toString();
	}

	public double getxFlow() {
		return xFlow;
	}

	public void setxFlow(double xFlow) {
		this.xFlow = xFlow;
	}

	public double getyHeight() {
		return yHeight;
	}

	public void setyHeight(double yHeight) {
		this.yHeight = yHeight;
	}

	public double getyEta() {
		return yEta;
	}

	public void setyEta(double yEta) {
		this.yEta = yEta;
	}

}
