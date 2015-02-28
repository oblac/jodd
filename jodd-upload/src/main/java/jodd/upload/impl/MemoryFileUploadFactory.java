// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.upload.impl;

import jodd.upload.FileUploadFactory;
import jodd.upload.FileUpload;
import jodd.upload.MultipartRequestInputStream;

/**
 * Factory for {@link jodd.upload.impl.MemoryFileUpload}.
 */
public class MemoryFileUploadFactory implements FileUploadFactory {

	protected int maxFileSize = 102400;

	public int getMaxFileSize() {
		return maxFileSize;
	}

	/**
	 * Sets maximum file upload size. Setting to -1 will disable this constraint.
	 */
	public MemoryFileUploadFactory setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public FileUpload create(MultipartRequestInputStream input) {
		return new MemoryFileUpload(input, maxFileSize);
	}

}
