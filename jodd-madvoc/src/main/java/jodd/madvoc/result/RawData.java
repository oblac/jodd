// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.meta.RenderWith;
import jodd.util.MimeTypes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Raw data for raw results.
 */
@RenderWith(RawResult.class)
public class RawData extends RawResultData  {

	public RawData(InputStream inputStream, String mimeType, int length) {
		super(inputStream, null, mimeType, length);
	}

	public RawData(byte[] bytes, String mimeType) {
		super(new ByteArrayInputStream(bytes), null, mimeType, bytes.length);
	}

	public RawData(byte[] bytes) {
		this(bytes, MimeTypes.MIME_APPLICATION_OCTET_STREAM);
	}

}
