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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

/**
 * Converter for Java Properties to Jodd Props format.
 */
class PropertiesToProps {

	/**
	 * Convert Java Properties to Jodd Props format
	 *
	 * @param writer     Writer to write Props formatted file content to
	 * @param properties Properties to convert to Props format
	 * @param profiles   Properties per profile to convert and add to the Props format
	 * @throws IOException On any I/O error when writing to the writer
	 */
	void convertToWriter(final Writer writer, final Properties properties, final Map<String, Properties> profiles)
			throws IOException {
		final BufferedWriter bw = getBufferedWriter(writer);
		writeBaseAndProfileProperties(bw, properties, profiles);
		writeProfilePropertiesThatAreNotInTheBase(bw, properties, profiles);
		bw.flush();
	}

	private void writeProfilePropertiesThatAreNotInTheBase(final BufferedWriter bw, final Properties baseProperties,
														   final Map<String, Properties> profiles) throws IOException {

		for (final Map.Entry<String, Properties> entry : profiles.entrySet()) {
			final String profileName = entry.getKey();
			final Properties profileProperties = entry.getValue();

			for (final Object key : profileProperties.keySet()) {
				if (baseProperties.containsKey(key)) {
					continue;
				}

				final String keyString = key.toString();
				writeProfileProperty(bw, profileName, keyString, profileProperties.getProperty(keyString));
			}
		}
	}

	private BufferedWriter getBufferedWriter(final Writer writer) {
		final BufferedWriter bw;
		if (writer instanceof BufferedWriter) {
			bw = (BufferedWriter) writer;
		} else {
			bw = new BufferedWriter(writer);
		}
		return bw;
	}

	private void writeBaseAndProfileProperties(final BufferedWriter bw, final Properties baseProperties,
											   final Map<String, Properties> profiles) throws IOException {
		for (final Object key : baseProperties.keySet()) {
			final String keyString = key.toString();

			final String value = baseProperties.getProperty(keyString);
			writeBaseProperty(bw, keyString, value);
			writeProfilePropertiesOfKey(bw, keyString, profiles);
		}
	}

	private void writeProfilePropertiesOfKey(final BufferedWriter bw, final String key,
											 final Map<String, Properties> profiles) throws IOException {
		for (final Map.Entry<String, Properties> entry : profiles.entrySet()) {
			final Properties profileProperties = entry.getValue();
			if (!profileProperties.containsKey(key)) {
				continue;
			}
			final String profileName = entry.getKey();
			writeProfileProperty(bw, profileName, key, profileProperties.getProperty(key));
		}
	}

	private void writeProfileProperty(final BufferedWriter bw, final String profileName,
									  final String key, final String value)
			throws IOException {
		bw.write(key + '<' + profileName + '>' + '=' + value);
		bw.newLine();
	}

	private void writeBaseProperty(final BufferedWriter bw, final String key, final String value)
			throws IOException {
		bw.write(key + '=' + value);
		bw.newLine();
	}
}