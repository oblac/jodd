// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload.impl;

import jodd.servlet.upload.FileUploadFactory;
import jodd.servlet.upload.FileUpload;
import jodd.servlet.upload.MultipartRequestInputStream;

/**
 * Factory for {@link jodd.servlet.upload.impl.MemoryFileUpload}.
 */
public class MemoryFileUploadFactory implements FileUploadFactory {

	/**
	 * {@inheritDoc}
	 */
	public FileUpload create(MultipartRequestInputStream input) {
		return new MemoryFileUpload(input);
	}

}
