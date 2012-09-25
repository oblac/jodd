// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverterManager;
import junit.framework.TestCase;

import java.io.File;

public class TypeConverterManagerTest extends TestCase {

	public void testRegistration() {
		FileConverter fileTypeConverter = (FileConverter) TypeConverterManager.lookup(File.class);

		assertNotNull(fileTypeConverter);
		assertNull(fileTypeConverter.addonFileConverters);
	}
}
