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

package jodd.io.upload.impl;

import jodd.io.FastByteArrayOutputStream;
import jodd.io.upload.FileUpload;
import jodd.io.upload.MultipartRequestInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link FileUpload} that stores uploaded files in memory byte array.
 */
public class MemoryFileUpload extends FileUpload {

	MemoryFileUpload(final MultipartRequestInputStream input, final int maxFileSize) {
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
	public InputStream getFileInputStream() {
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