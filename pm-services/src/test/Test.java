package test;

import java.util.Date;

import com.siemens.storage.DBUtil;
import com.siemens.storage.TableRow;

public class Test {

	public static void main(String[] args) {

		for (int i = 20; i < 300; i++) {
			long time = new Date().getTime();
			TableRow row = new TableRow("calculated_kpi");
			row.add("AssetId", "pump1");
			row.add("Flow", i);
			row.add("TDH", i);
			row.add("Efficiency", i);
			row.add("Timestamp", time);
			TableRow rrow = new TableRow("refrence_kpi");
			rrow.add("AssetId", "pump1");
			rrow.add("Flow", i);
			rrow.add("TDH", i + 0.3);
			rrow.add("Efficiency", i + 0.3);
			rrow.add("Timestamp", time);
			
			DBUtil.insert(row);
			DBUtil.insert(rrow);
			System.out.println(i - 19 + " row calculate kpi inserted pump1");
			System.out.println(i - 19 + " row ref kpi inserted");
		}
	}
	
}
