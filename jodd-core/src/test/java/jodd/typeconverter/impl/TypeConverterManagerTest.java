package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverterManager;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TypeConverterManagerTest {

	@Test
	public void testRegistration() {
		FileConverter fileTypeConverter = (FileConverter) TypeConverterManager.lookup(File.class);

		assertNotNull(fileTypeConverter);
		assertNull(fileTypeConverter.addonFileConverters);
	}
}
