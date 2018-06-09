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

package jodd.props;

import jodd.core.JoddCore;
import jodd.exception.UncheckedException;
import jodd.io.FastCharArrayWriter;
import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.io.findfile.ClassScanner;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.Wildcard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


/**
 * Super properties: fast, configurable, supports (ini) sections, profiles.
 * <p>
 * Basic parsing rules:
 * <ul>
 * <li> By default, props files are UTF8 encoded.
 * <li> Leading and trailing spaces will be trimmed from section names and property names.
 * <li> Leading and/or trailing spaces may be trimmed from property values.
 * <li> You can use either equal sign (=) or colon (:) to assign property values
 * <li> Comments begin with either a semicolon (;), or a sharp sign (#) and extend to the end of line. It doesn't have to be the first character.
 * <li> A backslash (\) escapes the next character (e.g., \# is a literal #, \\ is a literal \).
 * <li> If the last character of a line is backslash (\), the value is continued on the next line with new line character included.
 * <li> \\uXXXX is encoded as character
 * <li> \t, \r and \f are encoded as characters
 * </ul>
 * <p>
 * Sections rules:
 * <ul>
 * <li> Section names are enclosed between [ and ].
 * <li> Properties following a section header belong to that section. Section name is added as a prefix to section properties.
 * <li> Section ends with empty section definition [] or with new section start
 * </ul>
 * <p>
 * Profiles rules:
 * <ul>
 * <li> Profile names are enclosed between &lt; and &gt; in property key.
 * <li> Each property key may contain zero, one or more profile definitions.
 * </ul>
 * <p>
 * Macro rules:
 * <ul>
 * <li> Profile values may contain references to other properties using ${ and }
 * <li> Inner references are supported
 * <li> References are resolved first in the profile context and then in the base props context.
 * </ul>
 */
public class Props implements Cloneable {

	private static final String DEFAULT_PROFILES_PROP = "@profiles";

	protected final PropsParser parser;

	protected final PropsData data;

	protected String activeProfilesProp = DEFAULT_PROFILES_PROP;

	protected String[] activeProfiles;

	protected volatile boolean initialized;

	/**
	 * Statis ctor.
	 */
	public static Props create() {
		return new Props();
	}


	/**
	 * Creates new props.
	 */
	public Props() {
		this(new PropsParser());
	}

	protected Props(final PropsParser parser) {
		this.parser = parser;
		this.data = parser.getPropsData();
	}

	/**
	 * Clones props by creating new instance and copying current configuration.
	 */
	@Override
	protected Props clone() {
		final PropsParser parser = this.parser.clone();
		final Props p = new Props(parser);

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

	// ---------------------------------------------------------------- configuration

	/**
	 * Sets new active profiles and overrides existing ones.
	 * By setting <code>null</code>, no active profile will be set.
	 * <p>
	 * Note that if some props are loaded <b>after</b>
	 * this method call, they might override active profiles
	 * by using special property for active profiles (<code>@profiles</code>).
	 */
	public Props setActiveProfiles(final String... activeProfiles) {
		initialized = false;
		this.activeProfiles = activeProfiles;
		return this;
	}

	/**
	 * Specifies the new line string when EOL is escaped.
	 * Default value is an empty string.
	 */
	public Props setEscapeNewLineValue(final String escapeNewLineValue) {
		parser.escapeNewLineValue = escapeNewLineValue;
		return this;
	}

	/**
	 * Specifies should the values be trimmed from the left.
	 * Default is <code>true</code>.
	 */
	public Props setValueTrimLeft(final boolean valueTrimLeft) {
		parser.valueTrimLeft = valueTrimLeft;
		return this;
	}

	/**
	 * Specifies should the values be trimmed from the right.
	 * Default is <code>true</code>.
	 */
	public Props setValueTrimRight(final boolean valueTrimRight) {
		parser.valueTrimRight = valueTrimRight;
		return this;
	}

	/**
	 * Defines if the prefix whitespaces should be ignored when value is split into the lines.
	 */
	public Props setIgnorePrefixWhitespacesOnNewLine(final boolean ignorePrefixWhitespacesOnNewLine) {
		parser.ignorePrefixWhitespacesOnNewLine = ignorePrefixWhitespacesOnNewLine;
		return this;
	}

	/**
	 * Skips empty properties as they don't exist.
	 */
	public Props setSkipEmptyProps(final boolean skipEmptyProps) {
		parser.skipEmptyProps = skipEmptyProps;
		data.skipEmptyProps = skipEmptyProps;
		return this;
	}

	/**
	 * Appends duplicate props.
	 */
	public Props setAppendDuplicateProps(final boolean appendDuplicateProps) {
		data.appendDuplicateProps = appendDuplicateProps;
		return this;
	}

	/**
	 * Ignore missing macros by replacing them with an empty string.
	 */
	public Props setIgnoreMissingMacros(final boolean ignoreMissingMacros) {
		data.ignoreMissingMacros = ignoreMissingMacros;
		return this;
	}

	/**
	 * Enables multiline values.
	 */
	public Props setMultilineValues(final boolean multilineValues) {
		parser.multilineValues = multilineValues;
		return this;
	}

	/**
	 * Parses input string and loads provided properties map.
	 */
	protected synchronized void parse(final String data) {
		initialized = false;
		parser.parse(data);
	}

	// ---------------------------------------------------------------- load

	/**
	 * Loads props from the string.
	 */
	public Props load(final String data) {
		parse(data);
		return this;
	}

	/**
	 * Loads props from the file. Assumes UTF8 encoding unless
	 * the file ends with '.properties', than it uses ISO 8859-1.
	 */
	public Props load(final File file) throws IOException {
		final String extension = FileNameUtil.getExtension(file.getAbsolutePath());
		final String data;
		if (extension.equalsIgnoreCase("properties")) {
			data = FileUtil.readString(file, StringPool.ISO_8859_1);
		} else {
			data = FileUtil.readString(file);
		}
		parse(data);
		return this;
	}

	/**
	 * Loads properties from the file in provided encoding.
	 */
	public Props load(final File file, final String encoding) throws IOException {
		parse(FileUtil.readString(file, encoding));
		return this;
	}

	/**
	 * Loads properties from input stream. Stream is not closed at the end.
	 */
	public Props load(final InputStream in) throws IOException {
		final Writer out = new FastCharArrayWriter();
		StreamUtil.copy(in, out);
		parse(out.toString());
		return this;
	}

	/**
	 * Loads properties from input stream and provided encoding.
	 * Stream is not closed at the end.
	 */
	public Props load(final InputStream in, final String encoding) throws IOException {
		final Writer out = new FastCharArrayWriter();
		StreamUtil.copy(in, out, encoding);
		parse(out.toString());
		return this;
	}

	/**
	 * Loads base properties from the provided java properties.
	 * Null values are ignored.
	 */
	public Props load(final Map<?, ?> p) {
		for (final Map.Entry<?, ?> entry : p.entrySet()) {
			final String name = entry.getKey().toString();
			final Object value = entry.getValue();
			if (value == null) {
				continue;
			}
			data.putBaseProperty(name, value.toString(), false);
		}
		return this;
	}

	/**
	 * Loads base properties from java Map using provided prefix.
	 * Null values are ignored.
	 */
	@SuppressWarnings("unchecked")
	public Props load(final Map<?, ?> map, final String prefix) {
		String realPrefix = prefix;
		realPrefix += '.';
		for (final Map.Entry entry : map.entrySet()) {
			final String name = entry.getKey().toString();
			final Object value = entry.getValue();
			if (value == null) {
				continue;
			}
			data.putBaseProperty(realPrefix + name, value.toString(), false);
		}
		return this;
	}

	/**
	 * Loads system properties with given prefix.
	 * If prefix is <code>null</code> it will not be ignored.
	 */
	public Props loadSystemProperties(final String prefix) {
		final Properties environmentProperties = System.getProperties();
		load(environmentProperties, prefix);
		return this;
	}

	/**
	 * Loads environment properties with given prefix.
	 * If prefix is <code>null</code> it will not be used.
	 */
	public Props loadEnvironment(final String prefix) {
		final Map<String, String> environmentMap = System.getenv();
		load(environmentMap, prefix);
		return this;
	}

	/**
	 * Loads props and properties from the classpath.
	 */
	public Props loadFromClasspath(final String... patterns) {
		ClassScanner.create()
			.registerEntryConsumer(entryData -> {
				String usedEncoding = JoddCore.encoding;
				if (StringUtil.endsWithIgnoreCase(entryData.name(), ".properties")) {
					usedEncoding = StringPool.ISO_8859_1;
				}

				final String encoding = usedEncoding;
				UncheckedException.runAndWrapException(() -> load(entryData.openInputStream(), encoding));
			})
			.includeResources(true)
			.ignoreException(true)
			.excludeCommonJars()
			.excludeAllEntries(true)
			.includeEntries(patterns)
			.scanDefaultClasspath()
			.start();
		return this;
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
	public String getBaseValue(final String key) {
		return getValue(key, StringPool.EMPTY_ARRAY);
	}

	/**
	 * Returns value of property, using active profiles, or {@code null} if property not found.
	 */
	public String getValue(final String key) {
		initialize();
		return data.lookupValue(key, activeProfiles);
	}

	/**
	 * Returns value of property, using active profiles or default value if not found.
	 */
	public String getValueOrDefault(final String key, final String defaultValue) {
		initialize();
		final String value = data.lookupValue(key, activeProfiles);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * Returns integer value of given property or {@code null} if property not found.
	 */
	public Integer getIntegerValue(final String key) {
		final String value = getValue(key);
		if (value == null) {
			return null;
		}
		return Integer.valueOf(value);
	}

	/**
	 * Returns integer value or default one if property not defined.
	 */
	public Integer getIntegerValue(final String key, final Integer defaultValue) {
		final String value = getValue(key);
		if (value == null) {
			return defaultValue;
		}
		return Integer.valueOf(value);
	}

	/**
	 * Returns long value of given property or {@code null} if property not found.
	 */
	public Long getLongValue(final String key) {
		final String value = getValue(key);
		if (value == null) {
			return null;
		}
		return Long.valueOf(value);
	}

	/**
	 * Returns long value or default one if property not defined.
	 */
	public Long getLongValue(final String key, final Long defaultValue) {
		final String value = getValue(key);
		if (value == null) {
			return defaultValue;
		}
		return Long.valueOf(value);
	}

	/**
	 * Returns double value of given property or {@code null} if property not found.
	 */
	public Double getDoubleValue(final String key) {
		final String value = getValue(key);
		if (value == null) {
			return null;
		}
		return Double.valueOf(value);
	}

	/**
	 * Returns double value or default one if property not defined.
	 */
	public Double getDoubleValue(final String key, final Double defaultValue) {
		final String value = getValue(key);
		if (value == null) {
			return defaultValue;
		}
		return Double.valueOf(value);
	}

	/**
	 * Returns boolean value of given property or {@code null} if property not found.
	 */
	public Boolean getBooleanValue(final String key) {
		final String value = getValue(key);
		if (value == null) {
			return null;
		}
		return Boolean.valueOf(value);
	}

	/**
	 * Returns boolean value or default one if property not defined.
	 */
	public Boolean getBooleanValue(final String key, final Boolean defaultValue) {
		final String value = getValue(key);
		if (value == null) {
			return defaultValue;
		}
		return Boolean.valueOf(value);
	}

	/**
	 * Returns <code>string</code> value of given profiles. If key is not
	 * found under listed profiles, base properties will be searched.
	 * Returns <code>null</code> if property doesn't exist.
	 */
	public String getValue(final String key, final String... profiles) {
		initialize();
		return data.lookupValue(key, profiles);
	}

	public Integer getIntegerValue(final String key, final String... profiles) {
		final String value = getValue(key, profiles);
		if (value == null) {
			return null;
		}
		return Integer.valueOf(value);
	}
	public Integer getIntegerValue(final String key, final Integer defaultValue, final String... profiles) {
		final String value = getValue(key, profiles);
		if (value == null) {
			return defaultValue;
		}
		return Integer.valueOf(value);
	}
	public Long getLongValue(final String key, final String... profiles) {
		final String value = getValue(key, profiles);
		if (value == null) {
			return null;
		}
		return Long.valueOf(value);
	}
	public Long getLongValue(final String key, final Long defaultValue, final String... profiles) {
		final String value = getValue(key, profiles);
		if (value == null) {
			return defaultValue;
		}
		return Long.valueOf(value);
	}
	public Double getDoubleValue(final String key, final String... profiles) {
		final String value = getValue(key, profiles);
		if (value == null) {
			return null;
		}
		return Double.valueOf(value);
	}
	public Double getDoubleValue(final String key, final Double defaultValue, final String... profiles) {
		final String value = getValue(key, profiles);
		if (value == null) {
			return defaultValue;
		}
		return Double.valueOf(value);
	}
	public Boolean getBooleanValue(final String key, final String... profiles) {
		final String value = getValue(key, profiles);
		if (value == null) {
			return null;
		}
		return Boolean.valueOf(value);
	}
	public Boolean getBooleanValue(final String key, final Boolean defaultValue, final String... profiles) {
		final String value = getValue(key, profiles);
		if (value == null) {
			return defaultValue;
		}
		return Boolean.valueOf(value);
	}


	/**
	 * Sets default value.
	 */
	public void setValue(final String key, final String value) {
		setValue(key, value, null);
	}

	/**
	 * Sets value on some profile.
	 */
	public void setValue(final String key, final String value, final String profile) {
		if (profile == null) {
			data.putBaseProperty(key, value, false);
		} else {
			data.putProfileProperty(key, value, profile, false);
		}
		initialized = false;
	}

	// ---------------------------------------------------------------- extract

	/**
	 * Extracts props belonging to active profiles.
	 */
	public void extractProps(final Map target) {
		initialize();
		data.extract(target, activeProfiles, null, null);
	}

	/**
	 * Extract props of given profiles.
	 */
	public void extractProps(final Map target, final String... profiles) {
		initialize();
		data.extract(target, profiles, null, null);
	}

	/**
	 * Extracts subset of properties that matches given wildcards.
	 */
	public void extractSubProps(final Map target, final String... wildcardPatterns) {
		initialize();
		data.extract(target, activeProfiles, wildcardPatterns, null);
	}

	/**
	 * Extracts subset of properties that matches given wildcards.
	 */
	public void extractSubProps(final Map target, final String[] profiles, final String[] wildcardPatterns) {
		initialize();
		data.extract(target, profiles, wildcardPatterns, null);
	}

	// ---------------------------------------------------------------- childMap

	/**
	 * Returns inner map from the props with given prefix. Keys in returned map
	 * will not have the prefix.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> innerMap(final String prefix) {
		initialize();
		return data.extract(null, activeProfiles, null, prefix);
	}

	/**
	 * Adds child map to the props on given prefix.
	 */
	public void addInnerMap(final String prefix, final Map<?, ?> map) {
		addInnerMap(prefix, map, null);
	}

	/**
	 * Adds child map to the props on given prefix.
	 */
	public void addInnerMap(String prefix, final Map<?, ?> map, final String profile) {
		if (!StringUtil.endsWithChar(prefix, '.')) {
			prefix += StringPool.DOT;
		}

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			String key = entry.getKey().toString();

			key = prefix + key;

			setValue(key, entry.getValue().toString(), profile);
		}
	}

	// ---------------------------------------------------------------- initialize

	/**
	 * Initializes props. By default it only resolves active profiles.
	 */
	protected void initialize() {
		if (!initialized) {
			synchronized (this) {
				if (!initialized) {

					resolveActiveProfiles();

					initialized = true;
				}
			}
		}
	}

	/**
	 * Resolves active profiles from special property.
	 * This property can be only a base property!
	 * If default active property is not defined, nothing happens.
	 * Otherwise, it will replace currently active profiles.
	 */
	protected void resolveActiveProfiles() {
		if (activeProfilesProp == null) {
			activeProfiles = null;
			return;
		}

		final PropsEntry pv = data.getBaseProperty(activeProfilesProp);
		if (pv == null) {
			// no active profile set as the property, exit
			return;
		}

		final String value = pv.getValue();
		if (StringUtil.isBlank(value)) {
			activeProfiles = null;
			return;
		}

		activeProfiles = StringUtil.splitc(value, ',');
		StringUtil.trimAll(activeProfiles);
	}

	// ---------------------------------------------------------------- iterator

	/**
	 * Returns all profiles names.
	 */
	public String[] getAllProfiles() {
		String[] profiles = new String[data.profileProperties.size()];

		int index = 0;
		for (String profileName : data.profileProperties.keySet()) {
			profiles[index] = profileName;
			index++;
		}
		return profiles;
	}

	/**
	 * Returns all the profiles that define certain prop's key name.
	 * Key name is given as a wildcard, or it can be matched fully.
	 */
	public String[] getProfilesFor(final String propKeyNameWildcard) {
		HashSet<String> profiles = new HashSet<>();

		profile:
		for (Map.Entry<String, Map<String, PropsEntry>> entries : data.profileProperties.entrySet()) {
			String profileName = entries.getKey();

			Map<String, PropsEntry> value = entries.getValue();

			for (String propKeyName : value.keySet()) {
				if (Wildcard.equalsOrMatch(propKeyName, propKeyNameWildcard)) {
					profiles.add(profileName);
					continue profile;
				}
			}
		}

		return profiles.toArray(new String[0]);
	}

	/**
	 * Returns {@link PropsEntries builder} for entries {@link #iterator() itertor}.
	 */
	public PropsEntries entries() {
		initialize();
		return new PropsEntries(this);
	}

	/**
	 * Returns iterator for active profiles.
	 */
	public Iterator<PropsEntry> iterator() {
		return entries().activeProfiles().iterator();
	}

}