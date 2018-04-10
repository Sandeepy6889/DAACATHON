package oracle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OracleConfiguration {

	private static Properties dbProperties;

	public static Properties getDbProperties() {
		if (dbProperties == null)
			dbProperties = new OracleConfiguration().loadProperties();
		return dbProperties;
	}

	private Properties loadProperties() {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("oracle.cfg");
		Properties dbProperties = new Properties();
		try {
			dbProperties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dbProperties;
	}
}
