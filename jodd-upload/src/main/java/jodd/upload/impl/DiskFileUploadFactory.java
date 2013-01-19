// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

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
		this(SystemUtil.getTempDir());
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
			destFolder = SystemUtil.getTempDir();
		}
		File destination = new File(destFolder);
		if (destination.exists() == false) {
			destination.mkdirs();
		}
		if (destination.isDirectory() == false) {
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
