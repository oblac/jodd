// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.bean.BeanTemplate;
import jodd.bean.BeanTemplateResolver;
import jodd.io.FastCharArrayWriter;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.util.StringPool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;


/**
 * Super properties: fast, configurable, supports (ini) sections, profiles.
 *
 * <p>
 * Basic parsing rules:
 * <li> By default, props files are UTF8 encoded .
 * <li> Leading and trailing spaces will be trimmed from section names and property names.
 * <li> Leading and/or trailing spaces may be trimmed from property values.
 * <li> You can use either equal sign (=) or colon (:) to assign property values
 * <li> Comments begin with either a semicolon (;), or a sharp sign (#) and extend to the end of line. It doesn't have to be the first character.
 * <li> A backslash (\) escapes the next character (e.g., \# is a literal #, \\ is a literal \).
 * <li> If the last character of a line is backslash (\), the value is continued on the next line with new line character included.
 *
 * <p>
 * Sections rules:
 * <li> Section names are enclosed between [ and ].
 * <li> Properties following a section header belong to that section. Section name is added as a prefix to section properties.
 * <li> Section ends with empty section definition [] or with new section start
 *
 * <p>
 * Profiles rules:
 * <li> Profile names are enclosed between ( and ) in property key.
 * <li> Each property key may contain zero, one or more profile definitions.
 *
 * <p>
 * Macro rules:
 * <li> Profile values may contain references to other properties using ${ and }
 * <li> Inner references are supported
 * <li> References are resolved first in the profile context and then in the base props context.
 */
public class Props implements Cloneable {

	private static final int MAX_INNER_MACROS = 100;

	protected final Map<String, String> properties;					// base properties
	protected final Map<String, Map<String, String>> profiles;		// profile properties
	protected final PropsParser parser;								// parser

	/**
	 * Creates new props.
	 */
	public Props() {
		properties = new HashMap<String, String>();
		profiles = new HashMap<String, Map<String, String>>();
		parser = new PropsParser(properties, profiles);
	}

	protected Props(Map<String, String> properties, Map<String, Map<String, String>> profiles, PropsParser parser) {
		this();
		this.properties.putAll(properties);
		for (Map.Entry<String, Map<String, String>> entry : profiles.entrySet()) {
			Map<String, String> map = new HashMap<String, String>(entry.getValue().size());
			map.putAll(entry.getValue());
			this.profiles.put(entry.getKey(), map);
		}

		this.parser.escapeNewLineValue = parser.escapeNewLineValue;
		this.parser.valueTrimLeft = parser.valueTrimLeft;
		this.parser.valueTrimRight = parser.valueTrimRight;
		this.parser.ignorePrefixWhitespacesOnNewLine = parser.ignorePrefixWhitespacesOnNewLine;
		this.parser.skipEmptyProps = parser.skipEmptyProps;
	}

	/**
	 * Clones props by creating new instance using
	 */
	@Override
	protected Props clone() {
		return new Props(properties, profiles, parser);
	}

	// ---------------------------------------------------------------- configuration

	/**
	 * Specifies the new line string when EOL is escaped.
	 * Default value is an empty string.
	 */
	public void setEscapeNewLineValue(String escapeNewLineValue) {
		parser.escapeNewLineValue = escapeNewLineValue;
	}

	/**
	 * Specifies should the values be trimmed from the left.
	 * Default is <code>true</code>.
	 */
	public void setValueTrimLeft(boolean valueTrimLeft) {
		parser.valueTrimLeft = valueTrimLeft;
	}

	/**
	 * Specifies should the values be trimmed from the right.
	 * Default is <code>true</code>.
	 */
	public void setValueTrimRight(boolean valueTrimRight) {
		parser.valueTrimRight = valueTrimRight;
	}

	/**
	 * Defines if the prefix whitespaces should be ignored when value is split into the lines.
	 */
	public void setIgnorePrefixWhitespacesOnNewLine(boolean ignorePrefixWhitespacesOnNewLine) {
		parser.ignorePrefixWhitespacesOnNewLine = ignorePrefixWhitespacesOnNewLine;
	}

	/**
	 * Skip empty properties.
	 */
	public void setSkipEmptyProps(boolean skipEmptyProps) {
		parser.skipEmptyProps = skipEmptyProps;
	}

	// ---------------------------------------------------------------- load

	/**
	 * Parses input string and loads provided properties map..
	 */
	protected synchronized void parse(String data) {
		initialized = false;
		parser.parse(data);
	}

	/**
	 * Loads props from the string.
	 */
	public void load(String data) {
		parse(data);
	}

	/**
	 * Loads props from the file assuming UTF8 encoding.
	 */
	public void load(File file) throws IOException {
		parse(FileUtil.readString(file));
	}

	/**
	 * Loads properties from the file in provided encoding.
	 */
	public void load(File file, String encoding) throws IOException {
		parse(FileUtil.readString(file, encoding));
	}

	/**
	 * Loads properties from input stream. Stream is not closed at the end.
	 */
	public void load(InputStream in) throws IOException {
		Writer out = new FastCharArrayWriter();
		StreamUtil.copy(in, out);
		parse(out.toString());
	}

	/**
	 * Loads base properties from the provided java properties.
	 */
	@SuppressWarnings({"unchecked"})
	public void load(Properties p) {
		Enumeration<String> names = (Enumeration<String>) p.propertyNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			properties.put(name, p.getProperty(name));
		}
	}

	// ---------------------------------------------------------------- props

	/**
	 * Counts the total number of properties, including all profiles.
	 * This operation performs calculation each time and it might be
	 * more time consuming then expected.
	 */
	public int countTotalProperties() {
		HashSet<String> profileKeys = new HashSet<String>();

		for (Map<String, String> map : profiles.values()) {
			for (String key : map.keySet()) {
				if (properties.containsKey(key) == false) {
					profileKeys.add(key);
				}
			}
		}

		return properties.size() + profileKeys.size();
	}

	/**
	 * Returns <code>string</code> value of base property.
	 * Returns <code>null</code> if property doesn't exist.
	 */
	@SuppressWarnings({"NullArgumentToVariableArgMethod"})
	public String getValue(String key) {
		return getValue(key, null);
	}

	/**
	 * Returns <code>string</code> value of given profiles. If key is not
	 * found under listed profiles, base properties will be searched.
	 * Returns <code>null</code> if property doesn't exist.
	 */
	public String getValue(String key, String... profiles) {
		initialize();
		return lookupValue(key, profiles);
	}

	/**
	 * Lookup props value.
	 */
	protected String lookupValue(String key, String... profiles) {
		if (profiles != null) {
			for (String profile : profiles) {
				Map<String, String> profileMap = this.profiles.get(profile);
				if (profileMap == null) {
					continue;
				}
				String value = profileMap.get(key);
				if (value != null) {
					return value;
				}
			}
		}
		return properties.get(key);
	}

	// ---------------------------------------------------------------- extract

	/**
	 * Extract base props to properties.
	 */
	@SuppressWarnings({"NullArgumentToVariableArgMethod"})
	public Properties extractProperties() {
		return extractProperties(null);
	}

	/**
	 * Extract props to properties.
	 */
	public Properties extractProperties(String... profiles) {
		initialize();
		Properties properties = new Properties();
		if (profiles != null) {
			for (String profile : profiles) {
				Map<String, String> map = this.profiles.get(profile);
				if (map != null) {
					extract(properties, map);
				}
			}
		}
		extract(properties, this.properties);
		return properties;
	}

	protected void extract(Properties properties, Map<String, String> map) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String existingValue = properties.getProperty(key);
			if (existingValue == null) {
				properties.setProperty(key, entry.getValue());
			}
		}
	}

	// ---------------------------------------------------------------- initialize

	protected volatile boolean initialized;

	/**
	 * Initializes props by replacing macros in values with the lookup values.
	 */
	protected void initialize() {
		if (initialized == false) {
			synchronized (this) {
				if (initialized == false) {

					int loopCount = 0;

					while (loopCount++ < MAX_INNER_MACROS) {
						boolean replaced = resolveMacros(properties, null);

						for (Map.Entry<String, Map<String, String>> entry : profiles.entrySet()) {
							String profile = entry.getKey();
							Map<String, String> map = entry.getValue();
							replaced = replaced || resolveMacros(map, profile);
						}

						if (replaced == false) {
							break;
						}
					}

					initialized = true;
				}
			}
		}
	}

	protected boolean resolveMacros(Map<String, String> map, final String profile) {
		boolean replaced = false;
		BeanTemplateResolver resolver = new BeanTemplateResolver() {
			public Object resolve(String name) {
				return lookupValue(name, profile);
			}
		};
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String value = entry.getValue();
			String newValue = BeanTemplate.parse(value, resolver, StringPool.EMPTY);
			if (newValue.equals(value) == false) {
				entry.setValue(newValue);
				replaced = true;
			}
		}
		return replaced;
	}

}
                                                     