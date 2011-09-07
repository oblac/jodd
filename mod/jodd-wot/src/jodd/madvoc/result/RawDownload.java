// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import java.io.File;

/**
 * Download data for raw results.
 */
public class RawDownload extends RawResultData {

	public RawDownload(byte[] bytes, String downloadFileName, String mimeType) {
		super(bytes, downloadFileName, mimeType);
	}

	public RawDownload(byte[] bytes, String downloadFileName) {
		super(bytes, downloadFileName, null);
	}

}
