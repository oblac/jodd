// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverterManager;
import jodd.upload.FileUpload;
import junit.framework.TestCase;

import java.io.File;

public class UploadTypeConverterManagerAddonTest extends TestCase {

	public void testRegistration() {
		FileUploadConverter fileUploadConverter = (FileUploadConverter) TypeConverterManager.lookup(FileUpload.class);

		assertNotNull(fileUploadConverter);

		FileConverter fileTypeConverter = (FileConverter) TypeConverterManager.lookup(File.class);

		assertNotNull(fileTypeConverter);
		assertNotNull(fileTypeConverter.addonFileConverters);

	}

}
