// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload;

import java.io.IOException;
import java.io.InputStream;

/**
 * Encapsulates base for uploaded file. Its instance may be
 * either valid, when it represent an uploaded file, or invalid
 * when uploaded file doesn't exist or there was a problem with it.
 *
 * @see jodd.servlet.upload.MultipartRequest
 */
public abstract class FileUpload {

	protected MultipartRequestInputStream input;
	protected FileUploadHeader header;

	protected FileUpload(MultipartRequestInputStream input) {
		this.input = input;
		this.header = input.lastHeader;
	}

	// ----------------------------------------------------------------  header

	/**
	 * Returns {@link FileUploadHeader} of uploaded file.
	 */
	public FileUploadHeader getHeader() {
		return header;
	}

	// ---------------------------------------------------------------- data

	/**
	 * Returns all bytes of uploaded file. 
	 */
	public abstract byte[] getFileContent() throws IOException;

	/**
	 * Returns input stream of uploaded file.
	 */
	public abstract InputStream getFileInputStream() throws IOException;

	// ---------------------------------------------------------------- size and validity

	protected boolean uploaded = true;
	protected int size = -1;

	/**
	 * Returns the file upload size.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns <code>true</code> if file was uploaded correctly.
	 */
	public boolean isUploaded() {
		return uploaded;
	}

	// ---------------------------------------------------------------- process

	/**
	 * Process request input stream. Note that file size is unknown at this point.
	 * Therefore, the implementation <b>should</b> set the <b>size</b> attribute
	 * after successful processing.
	 *
	 * @see MultipartRequestInputStream
	 */
	protected abstract void processStream() throws IOException;

	// ---------------------------------------------------------------- toString

	/**
	 * Returns basic information about the uploaded file.
	 */
	@Override
	public String toString() {
		return "FileUpload: uploaded=[" + uploaded + "] field=[" + header.getFormFieldName() +
				"] name=[" + header.getFileName() + "] size=[" + size + ']';
	}
}