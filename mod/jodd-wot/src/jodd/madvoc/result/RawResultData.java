// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

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
public abstract class RawResultData {

	private static final String RESULT = RawResult.NAME + ':';

	protected final byte[] bytes;
	protected final String downloadFileName;
	protected final String mimeType;

	protected RawResultData(byte[] bytes, String downloadFileName, String mimeType) {
		this.bytes = bytes;
		this.downloadFileName = downloadFileName;
		this.mimeType = mimeType;
	}

	protected RawResultData(File file, String mimeType) {
		try {
			bytes = FileUtil.readBytes(file);
		} catch (IOException ioex) {
			throw new MadvocException("Unable to read file '" + file + "'.", ioex);
		}
		this.downloadFileName = file.getName();
		this.mimeType = mimeType;
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
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns download file name.
	 */
	public String getDownloadFileName() {
		return downloadFileName;
	}

	@Override
	public String toString() {
		return RESULT;
	}
}
