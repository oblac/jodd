// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload.impl;

import jodd.servlet.upload.FileUploadFactory;
import jodd.servlet.upload.FileUpload;
import jodd.servlet.upload.MultipartRequestInputStream;

/**
 * Factory for {@link jodd.servlet.upload.impl.MemoryFileUpload}.
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
