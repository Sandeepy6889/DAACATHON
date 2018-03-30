package com.siemens.pumpMonitoring.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PumpKPIResultSet implements DbRowToObject {

	private List<Object> pdata = new ArrayList<>();

	@Override
	public void fill(ResultSet rs) throws SQLException {
		PumpKPI kpi = new PumpKPI();
		kpi.setAssetID(rs.getString("AssetId"));
		kpi.setEfficiency(rs.getDouble("Efficiency"));
		kpi.setTDH(rs.getDouble("TDH"));
		kpi.setFlow(rs.getDouble("Flow"));
		kpi.setTimeStamp(rs.getDate("TimeStamp"));
		pdata.add(kpi);
	}

	@Override
	public List<Object> getData() {
		return pdata;
	}
}
