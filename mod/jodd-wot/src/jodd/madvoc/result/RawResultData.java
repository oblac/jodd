// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.util.MimeTypes;
import jodd.io.FileUtil;
import jodd.io.FileNameUtil;
import jodd.madvoc.MadvocException;

import java.io.File;
import java.io.IOException;

/**
 * Holder for Raw results.
 */
public class RawResultData {

	private static final String RESULT = RawResult.NAME + ':';

	protected final byte[] bytes;

	protected final String mime;

	public RawResultData(byte[] bytes, String mimeType) {
		this.bytes = bytes;
		this.mime = mimeType;
	}

	public RawResultData(byte[] bytes) {
		this(bytes, MimeTypes.MIME_APPLICATION_OCTET_STREAM);
	}

	public RawResultData(File file) {
		try {
			bytes = FileUtil.readBytes(file);
		} catch (IOException ioex) {
			throw new MadvocException("Unable to read file '" + file + "'.", ioex);
		}
		String extension = FileNameUtil.getExtension(file.getAbsolutePath());
		this.mime = MimeTypes.getMimeType(extension); 
	}

	/**
	 * Returns byte array.
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * Returns content type.
	 */
	public String getMime() {
		return mime;
	}

	@Override
	public String toString() {
		return RESULT;
	}
}
