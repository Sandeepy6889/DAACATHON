package com.siemens.primecult.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;

import com.siemens.primecult.oracle.OracleConnection;
import com.siemens.primecult.oracle.OracleDBOperation;
import com.siemens.primecult.xlsx.StreamingReader;
import com.siemens.storage.TableRow;

public class FileReader {

	private static final String DATA_FILE = "impellerFault_data.xlsx";
	private StreamingReader reader;
	private float[] data = null;

	public FileReader() {
		init();
	}

	private void init() {
		File inputWorkbook = new File(DATA_FILE);
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(inputWorkbook);
			reader = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).sheetIndex(0).read(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readVibrationData() {
		Iterator<Row> iterator = reader.iterator();
		Connection con = null;
		try {
		    con = OracleConnection.getDbConnection();
			con.setAutoCommit(false);
			int i = 1;
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			float value = (float) nextRow.getCell(0).getNumericCellValue();
			TableRow row = new TableRow("vibration");
			row.set("amplitude", value);
			System.out.println(value);
			OracleDBOperation.insert(con, i, value);
			System.out.println("Count "+i);
			i++;
		}
		con.commit();
		}catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
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
	
	private void insert() {
		Connection con = null;
		try {
		    con = OracleConnection.getDbConnection();
			con.setAutoCommit(false);
			for(int i=1;i<= 700000;i++) {
				OracleDBOperation.insert(con, i, 25560.988);
				con.commit();
				System.out.println("Count "+i);
			}
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

	public float[] getData() {
		return data == null ? new float[64] : data;
	}

	public void close() {
		reader.close();
	}

	public static void main(String[] args) {
		FileReader fr = new FileReader();
		fr.readVibrationData();
	}
}