// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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

	public FileUploadable(File file) {
		this.file = file;
		this.fileName = FileNameUtil.getName(file.getName());
		this.mimeType = null;
	}

	public FileUploadable(File file, String fileName, String mimeType) {
		this.file = file;
		this.fileName = fileName;
		this.mimeType = mimeType;
	}

	public File getContent() {
		return file;
	}

	public byte[] getBytes() {
		try {
			return FileUtil.readBytes(file);
		} catch (IOException ioex) {
			throw new HttpException(ioex);
		}
	}

	public String getFileName() {
		return fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public int getSize() {
		return (int) file.length();
	}

	public InputStream openInputStream() throws IOException {
		return new FileInputStream(file);
	}
}