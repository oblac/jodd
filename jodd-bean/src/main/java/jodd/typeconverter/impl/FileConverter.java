// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.typeconverter.impl;

import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.io.upload.FileUpload;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

	@Override
	public File convert(final Object value) {
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
