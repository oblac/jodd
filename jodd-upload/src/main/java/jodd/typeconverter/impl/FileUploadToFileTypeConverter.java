// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.typeconverter.TypeConverter;
import jodd.upload.FileUpload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Addon FileUpload to File type converter.
 */
public class FileUploadToFileTypeConverter implements TypeConverter<File> {

	public File convert(Object value) {
		if (value instanceof FileUpload) {
			FileUpload fileUpload = (FileUpload) value;

			InputStream in = null;
			try {
				in = fileUpload.getFileInputStream();

				File tempFile = FileUtil.createTempFile();

				FileUtil.writeStream(tempFile, in);

				return tempFile;
			} catch (IOException ioex) {
				return null;
			} finally {
				StreamUtil.close(in);
			}
		}
		return null;
	}

}
