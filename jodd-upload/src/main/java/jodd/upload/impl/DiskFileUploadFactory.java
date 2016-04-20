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

package jodd.upload.impl;

import jodd.upload.FileUpload;
import jodd.upload.MultipartRequestInputStream;
import jodd.upload.FileUploadFactory;
import jodd.util.SystemUtil;

import java.io.File;
import java.io.IOException;

/**
 * Factory for {@link jodd.upload.impl.DiskFileUpload}
 */
public class DiskFileUploadFactory implements FileUploadFactory {

	protected File destFolder;

	protected int maxFileSize = 102400; 

	public DiskFileUploadFactory() throws IOException {
		this(SystemUtil.tempDir());
	}

	public DiskFileUploadFactory(String destFolder) throws IOException {
		this(destFolder, 102400);

	}

	public DiskFileUploadFactory(String destFolder, int maxFileSize) throws IOException {
		setUploadDir(destFolder);
		this.maxFileSize = maxFileSize;
	}


	public DiskFileUploadFactory setUploadDir(String destFolder) throws IOException {
		if (destFolder == null) {
			destFolder = SystemUtil.tempDir();
		}
		File destination = new File(destFolder);
		if (!destination.exists()) {
			destination.mkdirs();
		}
		if (!destination.isDirectory()) {
			throw new IOException("Invalid destination folder: " + destFolder);
		}
		this.destFolder = destination;
		return this;
	}

	public int getMaxFileSize() {
		return maxFileSize;
	}

	/**
	 * Sets maximum file upload size. Setting to -1 will disable this constraint.
	 */
	public DiskFileUploadFactory setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public FileUpload create(MultipartRequestInputStream input) {
		return new DiskFileUpload(input, destFolder, maxFileSize);
	}

}
