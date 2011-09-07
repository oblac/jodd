// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.util.MimeTypes;

import java.io.File;

/**
 * Raw data for raw results.
 */
public class RawData extends RawResultData  {

	public RawData(byte[] bytes, String mimeType) {
		super(bytes, null, mimeType);
	}

	public RawData(byte[] bytes) {
		super(bytes, null, MimeTypes.MIME_APPLICATION_OCTET_STREAM);
	}

	public RawData(File file, String mimeType) {
		super(file, mimeType);
	}

	public RawData(File file) {
		super(file, MimeTypes.MIME_APPLICATION_OCTET_STREAM);
	}
}
