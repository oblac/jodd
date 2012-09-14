// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.URLConverter;

import java.io.File;
import java.net.URL;

public class URLConverterTest extends BaseTestCase {

	public void testConversion() {
		URLConverter urlConverter = new URLConverter();

		File f = new File("/folder/file.ext");
		URL url = urlConverter.convert(f);
		assertNotNull(url);
	}
}
