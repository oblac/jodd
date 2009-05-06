// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.util.StringPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

/**
 * Implements a DataSource from an InputStream, a byte array, a String, a File.
 *
 * This class has been build upon the similar one in javamail demos, but it
 * is enhanced.
 */
public class ByteArrayDataSource implements DataSource {

	protected byte[] data;		// data
	protected String type;		// content-type

	/**
	 * Create a datasource from a File. If the Content-Type parameter is null,
	 * the type will be derived from the filename extension.
	 *
	 * @param f File object
	 * @param type Content-Type
	 */
	public ByteArrayDataSource(File f, String type) throws IOException {
		this(new FileInputStream(f), type);
		if (this.type == null) {
			this.type = FileTypeMap.getDefaultFileTypeMap().getContentType(f);
		}
	}

	/**
	 * Create a datasource from an input stream.
	 *
	 * @param is InputStream
	 * @param type Content-Type
	 */
	public ByteArrayDataSource(InputStream is, String type) throws IOException {
		this.type = type;

		ByteArrayOutputStream os = new ByteArrayOutputStream(4096);

		byte buf[] = new byte[4096];
		int len;
		while (true) {
			len = is.read(buf);
			if (len < 0) {
				break;
			}
			os.write(buf, 0, len);
		}
		data = os.toByteArray();
	}

	/**
	 * Create a datasource from a byte array.
	 *
	 * @param data byte array
	 * @param type Content-Type
	 */
	public ByteArrayDataSource(byte[] data, String type) {
		this.type = type;
		this.data = data;
	}

	/**
	 * Create a datasource from a String. This method defaults to
	 * a String encoding of UTF-8. For a different encoding,
	 * specify a Mime "charset" in the Content-Type parameter.
	 *
	 * @param data byte array
	 * @param type Content-Type
	 */
	public ByteArrayDataSource(String data, String type) {
		this.type = type;
		try {
			this.data = data.getBytes(StringPool.UTF_8);
		} catch (UnsupportedEncodingException uex) {
			// ignore
		}
	}

	/**
	 * Return an InputStream to read the content.
	 *
	 * @return an InputStream with the content
	 */
	public InputStream getInputStream() throws IOException {
		if (data == null) {
			throw new IOException("No data.");
		}
		return new ByteArrayInputStream(data);
	}

	/**
	 * This DataSource cannot return an OutputStream, so this method is not
	 * implemented.
	 */
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("getOutputStream() not supported.");
	}

	/**
	 * Get the content type.
	 *
	 * @return Content-Type string
	 */
	public String getContentType() {
		return type;
	}

	/**
	 * Set the content type.
	 *
	 * @param type	Content-Type string
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

	/**
	 * Write the content to an OutputStream.
	 *
	 * @param os OutputStream to write the entire content to
	 */
	public void writeTo(OutputStream os) throws IOException {
		os.write(data);
	}

	/**
	 * Return the content as a byte array.
	 *
	 * @return byte array with the content
	 */
	public byte[] toByteArray() {
		return data;
	}

	/**
	 * Return the number of bytes in the content.
	 *
	 * @return size of the byte array, or -1 if not set.
	 */
	public int getSize() {
		if (data == null) {
			return -1;
		}
		else {
			return data.length;
		}
	}

	/**
	 * Return the content as a String. The Content-Type "charset" parameter
	 * will be used to determine the encoding, and if that's not available or
	 * invalid, UTF-8.
	 *
	 * @return a String with the content
	 */
	public String getText() {
		try {
			return new String(data, type);
		} catch (UnsupportedEncodingException uex) {
			try {
				return new String(data, StringPool.UTF_8);
			} catch (UnsupportedEncodingException uex1) {
				return null;
			}
		}
	}
}
