// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.servlet.upload.FileUpload;

/**
 * Converts given object to jodd.servlet.upload.FileUpload.
 */
public class FileUploadConverter implements TypeConverter<FileUpload> {

	public static FileUpload valueOf(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof FileUpload) {
			return (FileUpload) value;
		}
		throw new TypeConversionException(value);
	}

	public FileUpload convert(Object value) {
		return valueOf(value);
	}
	
}
