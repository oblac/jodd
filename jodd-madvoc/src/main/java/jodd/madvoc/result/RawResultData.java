// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.meta.RenderWith;

import java.io.InputStream;

/**
 * Holder for Raw results.
 */
@RenderWith(RawResult.class)
public abstract class RawResultData {

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

}