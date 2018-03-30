package com.siemens.primecult.constants;

public enum AlarmTypes {

	 TDH(0,""),
	 EFFICIENCY(1,""),
	 DRYRUN (2,"DRYRUN"),
	 BLOCKAGE(3,"BLOCKAGE");
	 
	 private int index;
	 private String value;
	
	 AlarmTypes(int index, String value){
		 this.index = index;
		 this.value = value;
	 }

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	 
	 

}
