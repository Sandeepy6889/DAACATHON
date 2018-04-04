package com.siemens.pumpMonitoring.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

	private static Properties dbProperties;

	public static Properties getDbProperties() {
		if (dbProperties == null)
			dbProperties = new Configuration().loadProperties();
		return dbProperties;
	}

	private Properties loadProperties() {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("database.cfg");
		Properties dbProperties = new Properties();
		try {
			dbProperties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dbProperties;
	}
}
