// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.URIConverter;
import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertNotNull;

public class URIConverterTest {

	@Test
	public void testConversion() {
		URIConverter uriConverter = new URIConverter();

		File f = new File("/folder/file.ext");
		URI uri = uriConverter.convert(f);
		assertNotNull(uri);
	}

}
