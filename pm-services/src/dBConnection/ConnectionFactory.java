package dBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {

	public static final String URL = "jdbc:mysql://localhost:3306/daac";
	public static final String USER = "ama";
	public static final String PASSWORD = "ama";

	/**
	 * Get a connection to database
	 * 
	 * @return Connection object
	 */
	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		return null;
	}
	/**
	 * close connection to DB
	 * 
	 * @return Connection object
	 */
	public static void closeConnection(Connection con, Statement st, ResultSet rs) throws Exception 
	{
		if (con != null)
			con.close();
		if (st != null)
			st.close();
		if (rs != null)
			rs.close();
	}
}
