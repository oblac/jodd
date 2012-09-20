// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import java.io.InputStream;

/**
 * Holder for Raw results.
 */
public abstract class RawResultData {

	private static final String RESULT = RawResult.NAME + ':';

	protected final InputStream inputStream;
	protected final String downloadFileName;
	protected final String mimeType;
	protected final int length;

	protected RawResultData(InputStream inputStream, String downloadFileName, String mimeType, int length) {
		this.inputStream = inputStream;
		this.downloadFileName = downloadFileName;
		this.mimeType = mimeType;
		this.length = length;
	}

	/**
	 * Returns content input stream.
	 */
	public InputStream getContentInputStream() {
		return inputStream;
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

	/**
	 * Returns content length.
	 */
	public int getContentLength() {
		return length;
	}

	@Override
	public String toString() {
		return RESULT;
	}
}
