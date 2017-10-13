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
