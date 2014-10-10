// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http.up;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Uploadable wrapper of <code>byte array</code>.
 */
public class ByteArrayUploadable implements Uploadable<byte[]> {

	protected final byte[] byteArray;
	protected final String fileName;
	protected final String mimeType;

	public ByteArrayUploadable(byte[] byteArray, String fileName) {
		this.byteArray = byteArray;
		this.fileName = fileName;
		this.mimeType = null;
	}

	public ByteArrayUploadable(byte[] byteArray, String fileName, String mimeType) {
		this.byteArray = byteArray;
		this.fileName = fileName;
		this.mimeType = mimeType;
	}

	public byte[] getContent() {
		return byteArray;
	}

	public byte[] getBytes() {
		return byteArray;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public int getSize() {
		return byteArray.length;
	}

	public InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(byteArray);
	}
}