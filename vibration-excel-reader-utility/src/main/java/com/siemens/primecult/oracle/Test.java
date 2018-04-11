package com.siemens.primecult.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Test {

	public static void main1(String[] args) {
		Connection con = null;
		try {
		    con = OracleConnection.getDbConnection();
			con.setAutoCommit(false);
			for(int i=1;i<= 700000;i++) {
				OracleDBOperation.insert(con, i, 25560.988);
				System.out.println("Count "+i);
			}
			con.commit();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("Rollback");
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		finally {
			if(con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	
	public static void main(String[] args) {
		Connection con = null;
		try {
		    con = OracleConnection.getDbConnection();
		    for(int i = 1; i<20;i++) {
		    	List<Double> list = OracleDBOperation.get(con, i * 10, i*10 + 10);
		    	System.out.println(list);
		    }
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		finally {
			
			if(con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
}
