// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload.impl;

import jodd.io.FastByteArrayOutputStream;
import jodd.servlet.upload.FileUpload;
import jodd.servlet.upload.MultipartRequestInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;

/**
 * {@link FileUpload} that stores uploaded files in memory byte array.
 */
public class MemoryFileUpload extends FileUpload {

	MemoryFileUpload(MultipartRequestInputStream input) {
		super(input);
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
	 * Returns buffered byte array stream.
	 */
	@Override
	public InputStream getFileInputStream() throws IOException {
		return new BufferedInputStream(new ByteArrayInputStream(data));
	}


	/**
	 * Reads data from input stream into byte array and stores file size.
	 */
	@Override
	public void processStream() throws IOException {
		FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
		input.copyAll(fbos);
		data = fbos.toByteArray();
		this.size = data.length;
	}

}