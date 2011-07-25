// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload.impl;

import jodd.servlet.upload.FileUpload;
import jodd.servlet.upload.MultipartRequestInputStream;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;

/**
 * {@link FileUpload} that saves uploaded files directly to destination folder.
 */
public class DiskFileUpload extends FileUpload {

	protected final File destFolder;

	DiskFileUpload(MultipartRequestInputStream input, File destinationFolder, int maxFileSize) {
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
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
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
