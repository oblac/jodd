// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.URLConverter;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

public class URLConverterTest {

	@Test
	public void testConversion() {
		URLConverter urlConverter = new URLConverter();

		File f = new File("/folder/file.ext");
		URL url = urlConverter.convert(f);
		assertNotNull(url);
	}
}
