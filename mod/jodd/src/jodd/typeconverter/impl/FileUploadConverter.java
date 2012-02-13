// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.servlet.upload.FileUpload;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to {@link FileUpload}.
 * Conversion rules:
 * <li><code>null</code> value is returned as <code>null</code>
 * <li>object of destination type is simply casted
 */
public class FileUploadConverter implements TypeConverter<FileUpload> {

	public FileUpload convert(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof FileUpload) {
			return (FileUpload) value;
		}
		throw new TypeConversionException(value);
	}

}
