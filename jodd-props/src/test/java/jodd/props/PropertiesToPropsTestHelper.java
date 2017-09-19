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

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PropertiesToPropsTestHelper {

	private PropertiesToPropsTestHelper() {
	}

	public static String safelyWritePropertiesToProps(final Properties baseProperties) throws IOException {
		final StringWriter writer = new StringWriter();
		safelyWritePropertiesToProps(writer, baseProperties);
		return writer.toString();
	}

	public static void safelyWritePropertiesToProps(final Writer writer, final Properties baseProperties)
			throws IOException {
		try {
			PropsUtil.convert(writer, baseProperties);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// close quietly
			}
		}
	}

	public static String safelyWritePropertiesToProps(final Properties baseProperties,
													  final Map<String, Properties> profiles) throws IOException {
		final StringWriter writer = new StringWriter();
		safelyWritePropertiesToProps(writer, baseProperties, profiles);
		return writer.toString();
	}

	public static void safelyWritePropertiesToProps(final Writer writer, final Properties baseProperties,
													final Map<String, Properties> profiles) throws IOException {
		try {
			PropsUtil.convert(writer, baseProperties, profiles);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// close quietly
			}
		}
	}

	// todo: implement equals and hashCode in Jodd Props, PropsData and PropsEntry then use assertEqualProps
	public static void assertEqualProps(final String actual, final String expectedResourceFileName) {
		final Props actualProps = new Props();
		actualProps.load(actual);

		final Props expectedProps = new Props();
		try {
			expectedProps.load(getResourceFile(expectedResourceFileName));
		} catch (IOException e) {
			fail(e.getMessage());
			throw new IllegalStateException(e);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
			throw new IllegalStateException(e);
		}
		assertEquals(expectedProps, actualProps);
	}

	public static void assertEqualsToPropsFile(final String actual, final String resourceNameWithExpectedFileContent)
			throws URISyntaxException {
		assertEqualsToPropsFile(actual, getResourceFile(resourceNameWithExpectedFileContent));
	}

	public static File getResourceFile(final String name) throws URISyntaxException {
		final URL resourceFile = PropertiesToPropsTestHelper.class.getResource(name);
		return new File(resourceFile.toURI());
	}

	public static void assertEqualsToPropsFile(final String actual, final File expectedFile) {
		final FileReader reader = getFileReader(expectedFile);
		final BufferedReader bufferedReader = new BufferedReader(reader);
		final String expected;
		try {
			expected = readContent(bufferedReader);
		} catch (IOException e) {
			fail(e.getMessage());
			throw new IllegalStateException(e);
		}

		String actualUnixStyle = actual.replace("\r\n", "\n");
		assertEquals(expected, actualUnixStyle);
	}

	private static String readContent(final BufferedReader reader) throws IOException {
		String content = "";
		String line;
		while ((line = reader.readLine()) != null) {
			content += line;
			// Mimic Props writer functionality
			content += '\n';
		}
		return content;
	}

	private static FileReader getFileReader(final File file) {
		final FileReader reader;
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
			throw new IllegalStateException(e);
		}
		return reader;
	}
}
