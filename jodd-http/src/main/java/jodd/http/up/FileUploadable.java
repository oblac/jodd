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

package jodd.http.up;

import jodd.http.HttpException;
import jodd.io.FileNameUtil;
import jodd.io.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * File uploadable.
 */
public class FileUploadable implements Uploadable<File> {

	protected final File file;
	protected final String fileName;
	protected final String mimeType;

	public FileUploadable(final File file) {
		this.file = file;
		this.fileName = FileNameUtil.getName(file.getName());
		this.mimeType = null;
	}

	public FileUploadable(final File file, final String fileName, final String mimeType) {
		this.file = file;
		this.fileName = fileName;
		this.mimeType = mimeType;
	}

	@Override
	public File getContent() {
		return file;
	}

	@Override
	public byte[] getBytes() {
		try {
			return FileUtil.readBytes(file);
		} catch (IOException ioex) {
			throw new HttpException(ioex);
		}
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public int getSize() {
		return (int) file.length();
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return new FileInputStream(file);
	}
}