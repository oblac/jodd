// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverterManagerBean;
import jodd.upload.FileUpload;

import java.io.File;

/**
 * Binder with the <code>jodd-bean</code> module.
 */
public class FileUploadBinder {

	/**
	 * Registers type converters.
	 */
	public static void registerTypeConverter(Object object) {
		TypeConverterManagerBean typeConverterManagerBean = (TypeConverterManagerBean) object;

		typeConverterManagerBean.register(FileUpload.class, new FileUploadConverter());

		FileConverter fileConverter = (FileConverter) typeConverterManagerBean.lookup(File.class);

		fileConverter.registerAddonConverter(new FileUploadToFileTypeConverter());
	}
}