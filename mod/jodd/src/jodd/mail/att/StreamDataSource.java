// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail.att;

import jodd.mail.MailException;
import jodd.util.StringPool;
import jodd.JoddDefault;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

/**
 * Simple <code>DataSource</code> implementation.
 */
public class StreamDataSource implements DataSource {

	protected InputStream inputStream;	// input stream
	protected String type;				// content-type

	/**
	 * Create a datasource from a File. If the Content-Type parameter is null,
	 * the type will be derived from the filename extension.
	 */
	public StreamDataSource(File file, String contentType) {
		if (contentType == null) {
			contentType = FileTypeMap.getDefaultFileTypeMap().getContentType(file);
		}
		this.type = contentType;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException fnfex) {
			throw new MailException(fnfex);
		}
	}

	/**
	 * Create a datasource from an input stream.
	 */
	public StreamDataSource(InputStream is, String type) {
		this.type = type;
		this.inputStream = is;
	}

	/**
	 * Create a datasource from a byte array.
	 */
	public StreamDataSource(byte[] data, String contentType) {
		this.type = contentType;
		this.inputStream = new ByteArrayInputStream(data);
	}

	/**
	 * Create a datasource from a String. This method defaults to
	 * a {@link JoddDefault#encoding default String encoding}.
	 */
	public StreamDataSource(String data, String contentType) {
		this(data, contentType, JoddDefault.encoding);
	}

	public StreamDataSource(String data, String type, String encoding) {
		this.type = type;
		byte[] bytes;
		try {
			bytes = data.getBytes(encoding);
		} catch (UnsupportedEncodingException uex) {
			throw new MailException("Invalid encoding", uex);
		}
		this.inputStream = new ByteArrayInputStream(bytes);
	}

	/**
	 * Return an InputStream to read the content.
	 */
	public InputStream getInputStream() throws IOException {
		if (inputStream == null) {
			throw new IOException("No data.");
		}
		return inputStream;
	}

	/**
	 * This DataSource cannot return an OutputStream,
	 * so this method is not implemented.
	 */
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("getOutputStream() not supported.");
	}

	/**
	 * Get the content type.
	 */
	public String getContentType() {
		return type;
	}

	/**
	 * Set the content type.
	 */
	public void setContentType(String type) {
		this.type = type;
	}

	/**
	 * Not implemented.
	 */
	public String getName() {
		return StringPool.EMPTY;
	}

}
