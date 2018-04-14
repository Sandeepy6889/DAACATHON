package com.siemens.primecult.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OracleDBOperation {

	public static void insert(Connection connection, int id, double amplitude) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into vibration(id, amplitude) values(?,?)");
		statement.setInt(1, id);
		statement.setDouble(2, amplitude);
		System.out.println("Row affected "+statement.executeUpdate());
		statement.close();
	}
	
	public static void insert(Connection connection, int id,String assetId,  double ff, double ps, double pd, double pwr) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into pump_data(id, asset_id, ff, ps, pd, pwr ) values(?,?,?,?,?,?)");
		statement.setInt(1, id);
		statement.setString(2, assetId);
		statement.setDouble(3, ff);
		statement.setDouble(4, ps);
		statement.setDouble(5, pd);
		statement.setDouble(6, pwr);
		System.out.println("Row affected "+statement.executeUpdate());
		statement.close();
	}
	
	
	
	public static List<Double> get(Connection connection, int first, int last) throws SQLException {
		String query ="SELECT * FROM (SELECT ROWNUM rnum,a.* FROM (SELECT * FROM  vibration order by id asc) a) WHERE rnum BETWEEN "+first+" AND "+last;
		Statement stmt = connection.createStatement();
		ResultSet rs;
		rs = stmt.executeQuery(query);
		List<Double> list = new ArrayList<>();
		while (rs.next()) {
			list.add(rs.getDouble("amplitude"));
		}
		rs.close();
		stmt.close();
		return list;
	}
	
	
	public static float[] getPumpDataValues(Connection connection, int id) throws SQLException {
		String query ="SELECT * FROM pump_data WHERE id="+id;
		Statement stmt = connection.createStatement();
		ResultSet rs;
		rs = stmt.executeQuery(query);
		float[] data = new float[4];
		while (rs.next()) {
			data[0] = rs.getFloat("ff");
			data[1] = rs.getFloat("ps");
			data[2] = rs.getFloat("pd");
			data[3] = rs.getFloat("pwr");
		}
		rs.close();
		stmt.close();
		return data;
	}
// create table vibration(id number primary key,amplitude number(20,5))	
}
