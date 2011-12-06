// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.URIConverter;

import java.io.File;
import java.net.URI;

public class URIConverterTest extends BaseTestCase {

	public void testConversion() {
		File f = new File("/folder/file.ext");
		URI uri = URIConverter.valueOf(f);
		assertNotNull(uri);
	}

}
