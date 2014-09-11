// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.core.JoddCore;
import jodd.io.findfile.ClassScanner;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Some {@link Props} utilities.
 */
public class PropsUtil {

	/**
	 * Loads props from classpath.
	 */
	public static void loadFromClasspath(final Props p, final String... patterns) {
		final ClassScanner scanner = new ClassScanner() {
			@Override
			protected void onEntry(EntryData entryData) throws IOException {
				String encoding = JoddCore.encoding;
				if (StringUtil.endsWithIgnoreCase(entryData.getName(), ".properties")) {
					encoding = StringPool.ISO_8859_1;
				}
				p.load(entryData.openInputStream(), encoding);
			}
		};
		scanner.setIncludeResources(true);
		scanner.setIgnoreException(true);
		scanner.setExcludeAllEntries(true);
		scanner.setIncludedEntries(patterns);
		scanner.scanDefaultClasspath();
	}

	/**
	 * Creates new props and {@link #loadFromClasspath(Props, String...) loads from classpath}.
	 */
	public static Props createFromClasspath(final String... patterns) {
		final Props p = new Props();
		loadFromClasspath(p, patterns);
		return p;
	}

	/**
	 * Convert Java Properties to Jodd Props format.
	 *
	 * @param writer     Writer to write Props formatted file content to
	 * @param properties Properties to convert to Props format
	 * @throws IOException On any I/O error when writing to the writer
	 */
	public static void convert(final Writer writer, final Properties properties) throws IOException {
		final Map<String, Properties> emptyProfiles = Collections.emptyMap();
		convert(writer, properties, emptyProfiles);
	}

	/**
	 * Convert Java Properties to Jodd Props format.
	 *
	 * @param writer     Writer to write Props formatted file content to
	 * @param properties Properties to convert to Props format
	 * @param profiles   Properties per profile to convert and add to the Props format
	 * @throws IOException On any I/O error when writing to the writer
	 */
	public static void convert(final Writer writer, final Properties properties, final Map<String, Properties> profiles)
			throws IOException {
		final PropertiesToProps toProps = new PropertiesToProps();
		toProps.convertToWriter(writer, properties, profiles);
	}
}
