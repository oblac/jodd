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

import jodd.io.upload.FileUpload;
import jodd.io.upload.FileUploadFactory;
import jodd.io.upload.MultipartRequestInputStream;

import java.io.File;

/**
 *
 * Factory for {@link AdaptiveFileUpload}.
 */
public class AdaptiveFileUploadFactory implements FileUploadFactory {

	protected int memoryThreshold = 8192;
	protected File uploadPath;
	protected int maxFileSize = 102400;
	protected boolean breakOnError;
	protected String[] fileExtensions;
	protected boolean allowFileExtensions = true;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileUpload create(final MultipartRequestInputStream input) {
		return new AdaptiveFileUpload(input, memoryThreshold, uploadPath, maxFileSize, breakOnError, fileExtensions, allowFileExtensions);
	}

	// ---------------------------------------------------------------- properties

	public int getMemoryThreshold() {
		return memoryThreshold;
	}
	/**
	 * Specifies per file memory limit for keeping uploaded files in the memory.
	 */
	public AdaptiveFileUploadFactory setMemoryThreshold(final int memoryThreshold) {
		if (memoryThreshold >= 0) {
			this.memoryThreshold = memoryThreshold;
		}
		return this;
	}

	public File getUploadPath() {
		return uploadPath;
	}

	/**
	 * Specifies the upload path. If set to <code>null</code> default
	 * system TEMP path will be used.
	 */
	public AdaptiveFileUploadFactory  setUploadPath(final File uploadPath) {
		this.uploadPath = uploadPath;
		return this;
	}

	public int getMaxFileSize() {
		return maxFileSize;
	}

	/**
	 * Sets maximum file upload size. Setting to <code>-1</code>
	 * disables this constraint.
	 */
	public AdaptiveFileUploadFactory setMaxFileSize(final int maxFileSize) {
		this.maxFileSize = maxFileSize;
		return this;
	}

	public boolean isBreakOnError() {
		return breakOnError;
	}

	public AdaptiveFileUploadFactory setBreakOnError(final boolean breakOnError) {
		this.breakOnError = breakOnError;
		return this;
	}

	/**
	 * Specifies if upload should break on error.
	 */
	public AdaptiveFileUploadFactory breakOnError(final boolean breakOnError) {
		this.breakOnError = breakOnError;
		return this;
	}

	/**
	 * Allow or disallow set of file extensions. Only one rule can be active at time,
	 * which means user can only specify extensions that are either allowed or disallowed.
	 * Setting this value to <code>null</code> will turn this feature off.
	 */
	public AdaptiveFileUploadFactory setFileExtensions(final String[] fileExtensions, final boolean allow) {
		this.fileExtensions = fileExtensions;
		this.allowFileExtensions = allow;
		return this;
	}

}
