// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.io.FastCharArrayWriter;
import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.typeconverter.Convert;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;


/**
 * Super properties: fast, configurable, supports (ini) sections, profiles.
 *
 * <p>
 * Basic parsing rules:
 * <li> By default, props files are UTF8 encoded.
 * <li> Leading and trailing spaces will be trimmed from section names and property names.
 * <li> Leading and/or trailing spaces may be trimmed from property values.
 * <li> You can use either equal sign (=) or colon (:) to assign property values
 * <li> Comments begin with either a semicolon (;), or a sharp sign (#) and extend to the end of line. It doesn't have to be the first character.
 * <li> A backslash (\) escapes the next character (e.g., \# is a literal #, \\ is a literal \).
 * <li> If the last character of a line is backslash (\), the value is continued on the next line with new line character included.
 * <li> \\uXXXX is encoded as character
 * <li> \t, \r and \f are encoded as characters
 *
 * <p>
 * Sections rules:
 * <li> Section names are enclosed between [ and ].
 * <li> Properties following a section header belong to that section. Section name is added as a prefix to section properties.
 * <li> Section ends with empty section definition [] or with new section start
 *
 * <p>
 * Profiles rules:
 * <li> Profile names are enclosed between &lt; and &gt; in property key.
 * <li> Each property key may contain zero, one or more profile definitions.
 *
 * <p>
 * Macro rules:
 * <li> Profile values may contain references to other properties using ${ and }
 * <li> Inner references are supported
 * <li> References are resolved first in the profile context and then in the base props context.
 */
public class Props implements Cloneable {

	private static final String DEFAULT_PROFILES_PROP = "@profiles";

	protected final PropsParser parser;									// parser
	protected final PropsData data;										// data

	protected String activeProfilesProp = DEFAULT_PROFILES_PROP;		// active profiles property
	protected String[] activeProfiles;

	/**
	 * Creates new props.
	 */
	public Props() {
		this(new PropsParser());
	}

	protected Props(PropsParser parser) {
		this.parser = parser;
		this.data = parser.getPropsData();
	}

	/**
	 * Clones props by creating new instance and copying current configuration.
	 */
	@Override
	protected Props clone() {
		PropsParser parser = this.parser.clone();
		Props p = new Props(parser);

		p.activeProfilesProp = activeProfilesProp;

		return p;
	}

	/**
	 * Returns active profiles or <code>null</code> if none defined.
	 */
	public String[] getActiveProfiles() {
		initialize();
		return activeProfiles;
	}

	/**
	 * Overrides active profiles.
	 */
	public void setActiveProfiles(String... activeProfiles) {
		initialize();
		this.activeProfiles = activeProfiles;
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
	 * Skips empty properties.
	 */
	public void setSkipEmptyProps(boolean skipEmptyProps) {
		parser.skipEmptyProps = skipEmptyProps;
	}

	/**
	 * Appends duplicate props.
	 */
	public void setAppendDuplicateProps(boolean appendDuplicateProps) {
		data.appendDuplicateProps = appendDuplicateProps;
	}

	/**
	 * Enables multiline values.
	 */
	public void setMultilineValues(boolean multilineValues) {
		parser.multilineValues = multilineValues;
	}

	// ---------------------------------------------------------------- load

	/**
	 * Parses input string and loads provided properties map.
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
	 * Loads props from the file. Assumes UTF8 encoding unless
	 * the file ends with '.properties', than it uses ISO 8859-1.
	 */
	public void load(File file) throws IOException {
		String extension = FileNameUtil.getExtension(file.getAbsolutePath());
		String data;
		if (extension.equalsIgnoreCase("properties")) {
			data = FileUtil.readString(file, StringPool.ISO_8859_1);
		} else {
			data = FileUtil.readString(file);
		}
		parse(data);
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
	 * Loads properties from input stream and provided encoding.
	 * Stream is not closed at the end.
	 */
	public void load(InputStream in, String encoding) throws IOException {
		Writer out = new FastCharArrayWriter();
		StreamUtil.copy(in, out, encoding);
		parse(out.toString());
	}

	/**
	 * Loads base properties from the provided java properties.
	 * Null values are ignored.
	 */
	public void load(Map<?, ?> p) {
		for (Map.Entry<?, ?> entry : p.entrySet()) {
			String name = entry.getKey().toString();
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}
			data.putBaseProperty(name, value.toString());
		}
	}

	/**
	 * Loads base properties from java Map using provided prefix.
	 * Null values are ignored.
	 */
	@SuppressWarnings("unchecked")
	public void load(Map<?, ?> map, String prefix) {
		prefix += '.';
		for (Map.Entry entry : map.entrySet()) {
			String name = entry.getKey().toString();
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}
			data.putBaseProperty(prefix + name, value.toString());
		}
	}

	/**
	 * Loads system properties with given prefix.
	 * If prefix is <code>null</code> it will not be ignored.
	 */
	public void loadSystemProperties(String prefix) {
		Properties environmentProperties = System.getProperties();
		load(environmentProperties, prefix);
	}

	/**
	 * Loads environment properties with given prefix.
	 * If prefix is <code>null</code> it will not be used.
	 */
	public void loadEnvironment(String prefix) {
		Map<String, String> environmentMap = System.getenv();
		load(environmentMap, prefix);
	}

	// ---------------------------------------------------------------- props

	/**
	 * Counts the total number of properties, including all profiles.
	 * This operation performs calculation each time and it might be
	 * more time consuming then expected.
	 */
	public int countTotalProperties() {
		return data.countBaseProperties() + data.countProfileProperties();
	}

	/**
	 * Returns <code>string</code> value of base property.
	 * Returns <code>null</code> if property doesn't exist.
	 */
	@SuppressWarnings({"NullArgumentToVariableArgMethod"})
	public String getBaseValue(String key) {
		return getValue(key, null);
	}

	/**
	 * Returns value of property, using active profiles.
	 */
	public String getValue(String key) {
		return getValue(key, activeProfiles);
	}

	/**
	 * Returns <code>string</code> value of given profiles. If key is not
	 * found under listed profiles, base properties will be searched.
	 * Returns <code>null</code> if property doesn't exist.
	 */
	public String getValue(String key, String... profiles) {
		initialize();
		return data.lookupValue(key, profiles);
	}


	// ---------------------------------------------------------------- put

	/**
	 * Sets default value.
	 */
	public void setValue(String key, String value) {
		setValue(key, value, null);
	}

	/**
	 * Sets value on some profile.
	 */
	public void setValue(String key, String value, String profile) {
		if (profile == null) {
			data.putBaseProperty(key, value);
		} else {
			data.putProfileProperty(key, value, profile);
		}
		initialized = false;
	}

	// ---------------------------------------------------------------- extract

	/**
	 * Extract base props (no profiles).
	 */
	public void extractBaseProps(Map target) {
		extractProps(target, null);
	}

	/**
	 * Extracts props belonging to active profiles.
	 */
	public void extractProps(Map target) {
		extractProps(target, activeProfiles);
	}

	/**
	 * Extract props of given profiles.
	 */
	public void extractProps(Map target, String... profiles) {
		initialize();
		data.extract(target, profiles, null);
	}

	public void extractBaseSubProps(Map target, String... wildcardPatterns) {
		initialize();
		data.extract(target, null, wildcardPatterns);
	}

	public void extractSubProps(Map target, String... wildcardPatterns) {
		initialize();
		data.extract(target, activeProfiles, wildcardPatterns);
	}

	public void extractSubProps(Map target, String[] profiles, String[] wildcardPatterns) {
		initialize();
		data.extract(target, profiles, wildcardPatterns);
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

					data.resolveMacros();
					resolveActiveProfiles();

					initialized = true;
				}
			}
		}
	}

	/**
	 * Resolves active profiles from property.
	 */
	protected void resolveActiveProfiles() {
		if (activeProfilesProp == null) {
			activeProfiles = null;
			return;
		}
		PropsValue pv = data.getBaseProperty(activeProfilesProp);
		if (pv == null) {
			activeProfiles = null;
			return;
		}
		String value = pv.getValue();
		if (StringUtil.isBlank(value)) {
			activeProfiles = null;
			return;
		}

		activeProfiles = Convert.toStringArray(value);
		StringUtil.trimAll(activeProfiles);
	}

}