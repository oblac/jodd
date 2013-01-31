// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.io.FileUtil;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.ArraysUtil;

import java.io.File;
import java.io.IOException;

/**
 * Converts given object into the <code>File</code>.
 * If created, returned file is stored in the temporary folder.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * <li><code>byte[]</code> content is used for creating a file</li>
 * <li><code>String</code> content is used for creating a file</li>
 * </ul>
 * <p>
 * This converter is plugable and add-on file converters from
 * other modules can be added.
 */
public class FileConverter implements TypeConverter<File> {

	protected TypeConverter<File>[] addonFileConverters;

	@SuppressWarnings("unchecked")
	public void registerAddonConverter(TypeConverter<File> fileTypeConverter) {
		if (addonFileConverters == null) {
			addonFileConverters = new TypeConverter[0];
		}

		ArraysUtil.append(addonFileConverters, fileTypeConverter);
	}

	public File convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof File) {
			return (File) value;
		}

		if (addonFileConverters != null) {
			for (TypeConverter<File> addonFileConverter : addonFileConverters) {
				File file = addonFileConverter.convert(value);

				if (file != null) {
					return file;
				}
			}
		}

/*
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
*/

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
