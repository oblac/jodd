// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.util;

import jodd.exception.UncheckedException;
import jodd.io.StreamUtil;
import jodd.io.findfile.ClassScanner;
import jodd.template.StringTemplateParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Misc java.util.Properties utils.
 */
public class PropertiesUtil {

	// ---------------------------------------------------------------- to/from files

	/**
	 * Create properties from the file.
	 *
	 * @param fileName properties file name to load
	 */
	public static Properties createFromFile(final String fileName) throws IOException {
		return createFromFile(new File(fileName));
	}

	/**
	 * Create properties from the file.
	 *
	 * @param file properties file to load
	 */
	public static Properties createFromFile(final File file) throws IOException {
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
	public static void loadFromFile(final Properties p, final String fileName) throws IOException {
		loadFromFile(p, new File(fileName));
	}

	/**
	 * Loads properties from the file. Properties are appended to the existing
	 * properties object.
	 *
	 * @param p      properties to fill in
	 * @param file   file to read properties from
	 */
	public static void loadFromFile(final Properties p, final File file) throws IOException {
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
	public static void writeToFile(final Properties p, final String fileName) throws IOException {
		writeToFile(p, new File(fileName), null);
	}

	/**
	 * Writes properties to a file.
	 *
	 * @param p        properties to write to file
	 * @param fileName destination file name
	 * @param header   optional header
	 */
	public static void writeToFile(final Properties p, final String fileName, final String header) throws IOException {
		writeToFile(p, new File(fileName), header);
	}

	/**
	 * Writes properties to a file.
	 *
	 * @param p      properties to write to file
	 * @param file   destination file
	 */
	public static void writeToFile(final Properties p, final File file) throws IOException {
		writeToFile(p, file, null);
	}

	/**
	 * Writes properties to a file.
	 *
	 * @param p      properties to write to file
	 * @param file   destination file
	 * @param header optional header
	 */
	public static void writeToFile(final Properties p, final File file, final String header) throws IOException {
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
	public static Properties createFromString(final String data) throws IOException {
		Properties p = new Properties();
		loadFromString(p, data);
		return p;
	}

	/**
	 * Loads properties from string.
	 */
	public static void loadFromString(final Properties p, final String data) throws IOException {
		try (ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StringPool.ISO_8859_1))) {
			p.load(is);
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
	public static Properties subset(final Properties p, String prefix, final boolean stripPrefix) {
		if (StringUtil.isBlank(prefix)) {
			return p;
		}
		if (!prefix.endsWith(StringPool.DOT)) {
			prefix += '.';
		}
		Properties result = new Properties();
		int baseLen = prefix.length();
		for (Object o : p.keySet()) {
			String key = (String) o;
			if (key.startsWith(prefix)) {
				result.setProperty(stripPrefix ? key.substring(baseLen) : key, p.getProperty(key));
			}
		}
		return result;
	}


	// ---------------------------------------------------------------- load from classpath

	/**
	 * Creates properties from classpath.
	 */
	public static Properties createFromClasspath(final String... rootTemplate) {
		Properties p = new Properties();
		return loadFromClasspath(p, rootTemplate);
	}

	/**
	 * Loads properties from classpath file(s). Properties are specified using wildcards.
	 */
	public static Properties loadFromClasspath(final Properties p, final String... rootTemplate) {
			ClassScanner.create()
				.registerEntryConsumer(entryData -> UncheckedException.runAndWrapException(() -> p.load(entryData.openInputStream())))
				.includeResources(true)
				.ignoreException(true)
				.excludeAllEntries(true)
				.includeEntries(rootTemplate)
				.scanDefaultClasspath();
		return p;
	}


	// ---------------------------------------------------------------- variables

	/**
	 * Returns String property from a map. If key is not found, or if value is not a String, returns <code>null</code>.
	 * Mimics <code>Property.getProperty</code> but on map.
	 */
	public static String getProperty(final Map map, final String key) {
		return getProperty(map, key, null);
	}

	/**
	 * Returns String property from a map.
	 * @see #getProperty(java.util.Map, String) 
	 */
	public static String getProperty(final Map map, final String key, final String defaultValue) {
		Object val = map.get(key);
		return (val instanceof String) ? (String) val : defaultValue;
	}

	/**
	 * Resolves all variables.
	 */
	public static void resolveAllVariables(final Properties prop) {
		for (Object o : prop.keySet()) {
			String key = (String) o;
			String value = resolveProperty(prop, key);
			prop.setProperty(key, value);
		}
	}

	private static final StringTemplateParser stp;

	static {
		stp = new StringTemplateParser();
		stp.setParseValues(true);
	}

	/**
	 * Returns property with resolved variables.
	 */
	public static String resolveProperty(final Map map, final String key) {
		String value = getProperty(map, key);
		if (value == null) {
			return null;
		}
		value = stp.parse(value, macroName -> getProperty(map, macroName));

		return value;
	}

}