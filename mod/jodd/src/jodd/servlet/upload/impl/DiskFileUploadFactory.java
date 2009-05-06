// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload.impl;

import jodd.servlet.upload.FileUpload;
import jodd.servlet.upload.MultipartRequestInputStream;
import jodd.servlet.upload.FileUploadFactory;
import jodd.util.SystemUtil;

import java.io.File;
import java.io.IOException;

/**
 * Factory for {@link jodd.servlet.upload.impl.DiskFileUpload}
 */
public class DiskFileUploadFactory implements FileUploadFactory {

	protected File destFolder;

	public DiskFileUploadFactory() throws IOException {
		this(SystemUtil.getTempDir());
	}

	public DiskFileUploadFactory(String destFolder) throws IOException {
		if (destFolder == null) {
			destFolder = SystemUtil.getTempDir();
		}
		File destination = new File(destFolder);
		if (destination.exists() == false) {
			destination.mkdirs();
		}
		if (destination.isDirectory() == false) {
			throw new IOException("Destination folder is invalid: '" + destFolder + "'.");
		}
		this.destFolder = destination;
	}

	/**
	 * {@inheritDoc}
	 */
	public FileUpload create(MultipartRequestInputStream input) {
		return new DiskFileUpload(input, destFolder);
	}

}
