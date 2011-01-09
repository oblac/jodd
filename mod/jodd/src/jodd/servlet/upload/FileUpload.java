// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

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

	protected final MultipartRequestInputStream input;
	protected final int maxFileSize;
	protected final FileUploadHeader header;

	protected FileUpload(MultipartRequestInputStream input, int maxFileSize) {
		this.input = input;
		this.header = input.lastHeader;
		this.maxFileSize = maxFileSize;
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

	protected boolean valid;

	protected int size = -1;

	protected boolean fileTooBig;

	/**
	 * Returns the file upload size or <code>-1</code>.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns <code>true</code> if file was uploaded correctly.
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Returns max file size or <code>-1</code> if there is no max file size. 
	 */
	public int getMaxFileSize() {
		return maxFileSize;
	}

	/**
	 * Returns <code>true</code> if file is too big. File will be marked as invalid.
	 */
	public boolean isFileTooBig() {
		return fileTooBig;
	}

	// ---------------------------------------------------------------- process

	/**
	 * Process request input stream. Note that file size is unknown at this point.
	 * Therefore, the implementation <b>should</b> set the {@link #getSize() size}
	 * attribute after successful processing. This method also must set the
	 * {@link #isValid() valid} attribute.
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
		return "FileUpload: valid=[" + valid + "] field=[" + header.getFormFieldName() +
				"] name=[" + header.getFileName() + "] size=[" + size + ']';
	}
}