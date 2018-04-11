package com.siemens.primecult.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.monitorjbl.xlsx.StreamingReader;
import com.siemens.storage.DBUtil;
import com.siemens.storage.TableRow;

public class FileReader {

	private static final String DATA_FILE = "vibrationData.xlsx";
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
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			float value = (float) nextRow.getCell(1).getNumericCellValue();
			TableRow row = new TableRow("vibration");
			row.set("amplitude", value);
			DBUtil.insert(row);

		}

	}

	public float[] getData() {
		return data == null ? new float[64] : data;
	}

	public void close() {
		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		FileReader fr = new FileReader();
		fr.readVibrationData();
	}
}