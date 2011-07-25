// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload.impl;

import jodd.io.FastByteArrayOutputStream;
import jodd.servlet.upload.FileUpload;
import jodd.servlet.upload.MultipartRequestInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * {@link FileUpload} that stores uploaded files in memory byte array.
 */
public class MemoryFileUpload extends FileUpload {

	MemoryFileUpload(MultipartRequestInputStream input, int maxFileSize) {
		super(input, maxFileSize);
	}

	// ---------------------------------------------------------------- logic

	protected byte[] data;

	/**
	 * Returns byte array containing uploaded file data.
	 */
	@Override
	public byte[] getFileContent() {
		return data;
	}

	/**
	 * Returns <code>true</code> as uploaded file is stored in memory.
	 */
	@Override
	public boolean isInMemory() {
		return true;
	}

	/**
	 * Returns byte array input stream.
	 */
	@Override
	public InputStream getFileInputStream() throws IOException {
		return new ByteArrayInputStream(data);
	}

	/**
	 * Reads data from input stream into byte array and stores file size.
	 */
	@Override
	public void processStream() throws IOException {
		FastByteArrayOutputStream out = new FastByteArrayOutputStream();
		size = 0;
		if (maxFileSize == -1) {
			size += input.copyAll(out);
		} else {
			size += input.copyMax(out, maxFileSize + 1);		// one more byte to detect larger files
			if (size > maxFileSize) {
				fileTooBig = true;
				valid = false;
				input.skipToBoundary();
				return;
			}
		}
		data = out.toByteArray();
		size = data.length;
		valid = true;
	}

}