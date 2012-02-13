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
 * If created, returned file is stored in the temporary folder.
 * Conversion rules:
 * <li><code>null</code> value is returned as <code>null</code>
 * <li>object of destination type is simply casted
 * <li><code>FileUpload</code> is read and converted
 * <li><code>byte[]</code> content is used for creating a file
 * <li><code>String</code> content is used for creating a file
 */
public class FileConverter implements TypeConverter<File> {

	public File convert(Object value) {
		if (value == null) {
			return null;
		}


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

		Class type = value.getClass();
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

}
