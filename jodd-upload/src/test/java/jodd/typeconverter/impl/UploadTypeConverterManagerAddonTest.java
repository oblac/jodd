package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverterManager;
import jodd.upload.FileUpload;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class UploadTypeConverterManagerAddonTest {

	@Test
	public void testRegistration() {
		FileUploadConverter fileUploadConverter = (FileUploadConverter) TypeConverterManager.lookup(FileUpload.class);

		assertNotNull(fileUploadConverter);

		FileConverter fileTypeConverter = (FileConverter) TypeConverterManager.lookup(File.class);

		assertNotNull(fileTypeConverter);
		assertNotNull(fileTypeConverter.addonFileConverters);

	}

}
