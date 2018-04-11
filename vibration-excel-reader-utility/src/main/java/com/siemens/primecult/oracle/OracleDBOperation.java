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
	
	public static List<Double> get(Connection connection, int first, int last) throws SQLException {
		String query ="SELECT * FROM (SELECT ROWNUM rnum,a.* FROM (SELECT * FROM  vibration order by id asc) a) WHERE rnum BETWEEN "+first+" AND "+last;
		Statement stmt = connection.createStatement();
		ResultSet rs;
		rs = stmt.executeQuery(query);
		List<Double> list = new ArrayList<>();
		while (rs.next()) {
			//list.add(rs.getDouble("amplitude"));
			list.add(Double.valueOf(rs.getInt("id")));
		}
		rs.close();
		stmt.close();
		return list;
	}
// create table vibration(id number primary key,amplitude number(20,5))	
}
