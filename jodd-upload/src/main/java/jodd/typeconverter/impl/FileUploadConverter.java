// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.upload.FileUpload;

/**
 * Converts given object to {@link FileUpload}.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * </ul>
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
