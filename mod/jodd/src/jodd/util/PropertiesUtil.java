// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.io.StreamUtil;
import jodd.io.AsciiInputStream;
import jodd.io.findfile.ClassScanner;
import static jodd.util.StringPool.DOLLAR_LEFT_BRACE;
import static jodd.util.StringPool.RIGHT_BRACE;
import static jodd.util.StringPool.DOLLAR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Map;

/**
 * Misc java.util.Properties utils.
 */
public class PropertiesUtil {

	private static final String SLASH_DOLLAR = "\\$";

	// ---------------------------------------------------------------- to/from files

	/**
	 * Create properties from the file.
	 *
	 * @param fileName properties file name to load
	 */
	public static Properties createFromFile(String fileName) throws IOException {
		return createFromFile(new File(fileName));
	}

	/**
	 * Create properties from the file.
	 *
	 * @param file properties file to load
	 */
	public static Properties createFromFile(File file) throws IOException {
		Properties prop = new Properties();
		loadFromFile(prop, file);
		return prop;
	}

	/**
	 * Loads properties from the file. Properties are appended to the existing
	 * properties object.
	 *
	 * @param p        properties to fill in
	 * @param fileName properties file name to load
	 */
	public static void loadFromFile(Properties p, String fileName) throws IOException {
		loadFromFile(p, new File(fileName));
	}

	/**
	 * Loads properties from the file. Properties are appended to the existing
	 * properties object.
	 *
	 * @param p      properties to fill in
	 * @param file   file to read properties from
	 */
	public static void loadFromFile(Properties p, File file) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			p.load(fis);
		} finally {
			StreamUtil.close(fis);
		}
	}


	/**
	 * Writes properties to a file.
	 *
	 * @param p        properties to write to file
	 * @param fileName destination file name
	 */
	public static void writeToFile(Properties p, String fileName) throws IOException {
		writeToFile(p, new File(fileName), null);
	}

	/**
	 * Writes properties to a file.
	 *
	 * @param p        properties to write to file
	 * @param fileName destination file name
	 * @param header   optional header
	 */
	public static void writeToFile(Properties p, String fileName, String header) throws IOException {
		writeToFile(p, new File(fileName), header);
	}

	/**
	 * Writes properties to a file.
	 *
	 * @param p      properties to write to file
	 * @param file   destination file
	 */
	public static void writeToFile(Properties p, File file) throws IOException {
		writeToFile(p, file, null);
	}

	/**
	 * Writes properties to a file.
	 *
	 * @param p      properties to write to file
	 * @param file   destination file
	 * @param header optional header
	 */
	public static void writeToFile(Properties p, File file, String header) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			p.store(fos, header);
		} finally {
			StreamUtil.close(fos);
		}
	}

	// ---------------------------------------------------------------- to/from string

	/**
	 * Creates properties from string.
	 */
	public static Properties createFromString(String data) throws IOException {
		Properties p = new Properties();
		loadFromString(p, data);
		return p;
	}

	/**
	 * Loads properties from string.
	 */
	public static void loadFromString(Properties p, String data) throws IOException {
		InputStream is = new AsciiInputStream(data);
		try {
			p.load(is);
		} finally {
			is.close();
		}
	}



	// ---------------------------------------------------------------- subsets

	/**
	 * Creates new Properties object from the original one, by copying
	 * those properties that have specified first part of the key name.
	 * Prefix may be optionally stripped during this process.
	 *
	 * @param p         source properties, from which new object will be created
	 * @param prefix    key names prefix 
	 *
	 * @return subset properties
	 */
	public static Properties subset(Properties p, String prefix, boolean stripPrefix) {
		if (StringUtil.isBlank(prefix) == true) {
			return p;
		}
		if (prefix.endsWith(StringPool.DOT) == false) {
			prefix += '.';
		}
		Properties result = new Properties();
		int baseLen = prefix.length();
		for (Object o : p.keySet()) {
			String key = (String) o;
			if (key.startsWith(prefix) == true) {
				result.setProperty(stripPrefix == true ? key.substring(baseLen) : key, p.getProperty(key));
			}
		}
		return result;
	}


	// ---------------------------------------------------------------- load from classpath

	/**
	 * Creates properties from classpath.
	 */
	public static Properties createFromClasspath(String... rootTemplate) {
		Properties p = new Properties();
		return loadFromClasspath(p, rootTemplate);
	}

	/**
	 * Loads properties from classpath file(s). Properties are specified using wildcards.
	 */
	public static Properties loadFromClasspath(final Properties p, String... rootTemplate) {
		ClassScanner scanner = new ClassScanner() {
			@Override
			protected void onEntry(EntryData entryData) throws IOException {
				p.load(entryData.openInputStream());
			}
		};
		scanner.setIncludeResources(true);
		scanner.setIgnoreException(true);
		scanner.setIncludedEntries(rootTemplate);
		scanner.scanDefaultClasspath();
		return p;
	}


	// ---------------------------------------------------------------- variables

	/**
	 * Returns String property from a map. If key is not found, or if value is not a String, returns <code>null</code>.
	 * Mimics <code>Property.getProperty</code> but on map.
	 */
	public static String getProperty(Map map, String key) {
		return getProperty(map, key, null);
	}

	/**
	 * Returns String property from a map.
	 * @see #getProperty(java.util.Map, String) 
	 */
	public static String getProperty(Map map, String key, String defaultValue) {
		Object val = map.get(key);
		return (val instanceof String) ? (String) val : defaultValue;
	}

	/**
	 * Resolves all variables.
	 */
	public static void resolveAllVariables(Properties prop) {
		for (Object o : prop.keySet()) {
			String key = (String) o;
			String value = resolveProperty(prop, key);
			prop.setProperty(key, value);
		}
	}

	/**
	 * Returns property with resolved variables.
	 */
	public static String resolveProperty(Map map, String key) {
		String value = getProperty(map, key);
		if (value == null) {
			return null;
		}
		int leftLen = DOLLAR_LEFT_BRACE.length();
		while (true) {
			int[] ndx = StringUtil.indexOfRegion(value, DOLLAR_LEFT_BRACE, RIGHT_BRACE, '\\');
			if (ndx == null) {
				break;
			}
			int innerNdx = StringUtil.lastIndexOf(value, DOLLAR_LEFT_BRACE, ndx[2], ndx[0]);
			if (innerNdx > ndx[0] + leftLen) {
				ndx[0] = innerNdx;
				ndx[1] = innerNdx + leftLen;
			}
			key = value.substring(ndx[1], ndx[2]);
			String inner = getProperty(map, key);
			value = value.substring(0, ndx[0]) + inner + value.substring(ndx[3]);
		}
		return StringUtil.replace(value, SLASH_DOLLAR, DOLLAR);
	}

}