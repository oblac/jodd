// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.servlet.upload.FileUpload;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Converts given object into the <code>File</code>.
 * Returned file is stored in temporary folder.
 */
public class FileConverter implements TypeConverter<File> {

	public static File valueOf(Object value) {
		if (value == null) {
			return null;
		}
		Class type = value.getClass();
		if (value instanceof File) {
			return (File) value;
		}
		if (value instanceof FileUpload) {
			FileUpload fileUpload = (FileUpload) value;

			InputStream in = null;
			try {
				in = fileUpload.getFileInputStream();
				File tempFile = FileUtil.createTempFile();
				FileUtil.writeStream(tempFile, in);
				return tempFile;
			} catch (IOException ioex) {
				throw new TypeConversionException(ioex);
			} finally {
				StreamUtil.close(in);
			}
		}
		if (type == byte[].class) {
			try {
				File tempFile = FileUtil.createTempFile();
				FileUtil.writeBytes(tempFile, (byte[])value);
				return tempFile;
			} catch (IOException ioex) {
				throw new TypeConversionException(ioex);
			}
		}
		if (type == String.class) {
			try {
				File tempFile = FileUtil.createTempFile();
				FileUtil.writeString(tempFile, value.toString());
				return tempFile;
			} catch (IOException ioex) {
				throw new TypeConversionException(ioex);
			}
		}
		throw new TypeConversionException(value);
	}

	public File convert(Object value) {
		return valueOf(value);
	}

}
