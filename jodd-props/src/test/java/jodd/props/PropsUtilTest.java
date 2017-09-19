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

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import static jodd.props.PropertiesToPropsTestHelper.assertEqualsToPropsFile;
import static jodd.props.PropertiesToPropsTestHelper.safelyWritePropertiesToProps;

public class PropsUtilTest {

	private static final String PROPSUTIL_CONVERT_PATH = "propsutil/convert";

	@Test
	public void canUseBufferedWriterToWriteBasePropertiesToProps() throws IOException, URISyntaxException {
		final Properties properties = new Properties();
		properties.setProperty("myOneProperty", "and it's value");

		final StringWriter stringWriter = new StringWriter();
		final BufferedWriter writer = new BufferedWriter(stringWriter);
		safelyWritePropertiesToProps(writer, properties);

		final String expectedResourceFileName = getResourcePath("/singleProperty.props");
		final String actual = stringWriter.toString();

//        assertEqualProps(actual, expectedResourceFileName);
		assertEqualsToPropsFile(actual, expectedResourceFileName);
	}

	@Test
	public void canWriteBasePropertiesToProps() throws IOException, URISyntaxException {
		final Properties properties = new Properties();
		properties.setProperty("myOneProperty", "and it's value");

		final String actual = safelyWritePropertiesToProps(properties);
		final String expectedResourceFileName = getResourcePath("singleProperty.props");

//        assertEqualProps(actual, expectedResourceFileName);
		assertEqualsToPropsFile(actual, expectedResourceFileName);
	}

	@Test
	public void canWriteBaseWithProfilePropertiesToProps() throws IOException, URISyntaxException {
		final Properties baseProperties = new Properties();
		baseProperties.setProperty("myOneProperty", "and it's value");

		final Properties productionProperties = new Properties();
		productionProperties.setProperty("myOneProperty", "I've got production written all over me");

		final Map<String, Properties> profiles = new LinkedHashMap<String, Properties>() {
			{
				put("production", productionProperties);
			}
		};
		final String actual = safelyWritePropertiesToProps(baseProperties, profiles);
		final String expectedResourceFileName = getResourcePath("oneProfile.props");

//        assertEqualProps(actual, expectedResourceFileName);
		assertEqualsToPropsFile(actual, expectedResourceFileName);
	}

	@Test
	public void canWriteBaseWithTwoProfilePropertiesToProps() throws IOException, URISyntaxException {
		final Properties baseProperties = new Properties();
		baseProperties.setProperty("myOneProperty", "and it's value");

		final Properties productionProperties = new Properties();
		productionProperties.setProperty("myOneProperty", "I've got production written all over me");

		final Properties testProperties = new Properties();
		testProperties.setProperty("myOneProperty", "TEST TEST TEST!!");

		final Map<String, Properties> profiles = new LinkedHashMap<String, Properties>() {
			{
				put("production", productionProperties);
				put("test", testProperties);
			}
		};
		final String actual = safelyWritePropertiesToProps(baseProperties, profiles);
		final String expectedResourceFileName = getResourcePath("twoProfiles.props");

//        assertEqualProps(actual, expectedResourceFileName);
		assertEqualsToPropsFile(actual, expectedResourceFileName);
	}

	@Test
	public void canWriteMoreProfileThanBasePropertiesToProps() throws IOException, URISyntaxException {
		final Properties baseProperties = new Properties();
		baseProperties.setProperty("myOneProperty", "and it's value");

		final Properties productionProperties = new Properties();
		productionProperties.setProperty("mySecondProperty", "I've got production written all over me");

		final Properties testProperties = new Properties();
		testProperties.setProperty("mySecondProperty", "TEST TEST TEST!!");

		final Map<String, Properties> profiles = new LinkedHashMap<String, Properties>() {
			{
				put("production", productionProperties);
				put("test", testProperties);
			}
		};
		final String actual = safelyWritePropertiesToProps(baseProperties, profiles);
		final String expectedResourceFileName = getResourcePath("moreProfilePropertiesThanBase.props");

//        assertEqualProps(actual, expectedResourceFileName);
		assertEqualsToPropsFile(actual, expectedResourceFileName);
	}

	@Test
	public void canWriteMultilineValuesToProps() throws IOException, URISyntaxException {
		final Properties baseProperties = new Properties();
		baseProperties.setProperty("myOneProperty", "long value\\\nin two lines");

		final String actual = safelyWritePropertiesToProps(baseProperties);
		final String expectedResourceFileName = getResourcePath("multilineValue.props");

//        assertEqualProps(actual, expectedResourceFileName);
		assertEqualsToPropsFile(actual, expectedResourceFileName);
	}

	@Test
	public void canWriteUtf8ValuesToProps() throws IOException, URISyntaxException {
		final Properties baseProperties = new Properties();
		baseProperties.setProperty("myOneProperty", "some utf8 \\u0161\\u0111\\u017e\\u010d\\u0107");

		final String actual = safelyWritePropertiesToProps(baseProperties);
		final String expectedResourceFileName = getResourcePath("utf8Value.props");

//        assertEqualProps(actual, expectedResourceFileName);
		assertEqualsToPropsFile(actual, expectedResourceFileName);
	}

	private String getResourcePath(final String name) {
		return PROPSUTIL_CONVERT_PATH + "/" + name;
	}

}
