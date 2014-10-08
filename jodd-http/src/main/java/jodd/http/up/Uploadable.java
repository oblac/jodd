// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http.up;

import java.io.IOException;
import java.io.InputStream;

/**
 * Common interface of uploaded content for {@link jodd.http.HttpBase#form() form parameters}.
 * All supported objects that can be uploaded using
 * the {@link jodd.http.HttpBase#form(String, Object)} has to
 * be wrapped with this interface.
 */
public interface Uploadable<T> {

	/**
	 * Returns the original content.
	 */
	public T getContent();

	/**
	 * Returns content bytes.
	 */
	public byte[] getBytes();

	/**
	 * Returns content file name.
	 * If <code>null</code>, the field's name will be used.
	 */
	public String getFileName();

	/**
	 * Returns MIME type. If <code>null</code>,
	 * MIME type will be determined from
	 * {@link #getFileName() file name's} extension.
	 */
	public String getMimeType();

	/**
	 * Returns size in bytes.
	 */
	public int getSize();

	/**
	 * Opens <code>InputStream</code>. User is responsible
	 * for closing it.
	 */
	public InputStream openInputStream() throws IOException;

}