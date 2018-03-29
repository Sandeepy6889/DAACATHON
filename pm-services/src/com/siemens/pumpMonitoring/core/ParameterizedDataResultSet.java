package com.siemens.pumpMonitoring.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParameterizedDataResultSet implements DbRowToObject{

	private List<Object> pdata = new ArrayList<>();
	
	@Override
	public void fill(ResultSet rs) throws SQLException {
		ParameterizedData param = new ParameterizedData();
		param.setAssetID(rs.getString("assetid"));
		param.setAssetName(rs.getString("assetname"));
		param.setMotorEfficiency(Double.valueOf(rs.getDouble("motorefficiency")));
		param.setRatedPower(Double.valueOf(rs.getDouble("ratedpower")));
		param.setMotorRatedSpeed(Double.valueOf(rs.getDouble("motorratedspeed")));
		param.setMinRatedFlowOfPump(Double.valueOf(rs.getDouble("minratedflowofpump")));
		param.setWaterDensity(Double.valueOf(rs.getDouble("waterdensity")));
		param.setThreadholdLT(Double.valueOf(rs.getDouble("threslt")));
		pdata.add(param);
	}
	
	public List<Object> getData() {
		return pdata;
	}

	/*create table paramdata(assetid varchar2(100) primary key,assetname varchar2(100), ratedpower number(10,2),motorefficiency number(10,2),
			motorratedspeed number(10,2),minratedflowofpump number(10,2),waterdensity number(10,2),threslt number(10,2));*/
	
}
