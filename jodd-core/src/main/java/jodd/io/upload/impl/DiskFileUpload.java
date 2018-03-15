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

import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.io.upload.FileUpload;
import jodd.io.upload.MultipartRequestInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link FileUpload} that saves uploaded files directly to destination folder.
 */
public class DiskFileUpload extends FileUpload {

	protected final File destFolder;

	DiskFileUpload(final MultipartRequestInputStream input, final File destinationFolder, final int maxFileSize) {
		super(input, maxFileSize);
		this.destFolder = destinationFolder;
	}

	/**
	 * Returns <code>false</code> as uploaded file is stored on disk.
	 */
	@Override
	public boolean isInMemory() {
		return false;
	}

	/**
	 * Returns destination folder.
	 */
	public File getDestinationFolder() {
		return destFolder;
	}

	/**
	 * Returns uploaded and saved file.
	 */
	public File getFile() {
		return file;
	}

	protected File file;

	/**
	 * Returns files content from disk file.
	 * If error occurs, it returns <code>null</code>
	 */
	@Override
	public byte[] getFileContent() throws IOException {
		return FileUtil.readBytes(file);
	}

	/**
	 * Returns new buffered file input stream.
	 */
	@Override
	public InputStream getFileInputStream() throws IOException {
		return new BufferedInputStream(new FileInputStream(file));
	}

	@Override
	protected void processStream() throws IOException {
		file = new File(destFolder, header.getFileName());
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

		size = 0;
		try {
			if (maxFileSize == -1) {
				size = input.copyAll(out);
			} else {
				size = input.copyMax(out, maxFileSize + 1);		// one more byte to detect larger files
				if (size > maxFileSize) {
					fileTooBig = true;
					valid = false;
					input.skipToBoundary();
					return;
				}
			}
			valid = true;
		} finally {
			StreamUtil.close(out);
		}
	}

}
