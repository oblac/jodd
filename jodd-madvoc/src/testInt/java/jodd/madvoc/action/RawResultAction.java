// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.result.RawData;
import jodd.util.MimeTypes;

@MadvocAction
public class RawResultAction {

	public static final byte[] SMALLEST_GIF = new byte[] {
		0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00,
		0x01, 0x00, 0x00, 0x00, 0x00, 0x2c, 0x00, 0x00,
		0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x02,
		0x02, 0x4c, 0x01, 0x00, 0x3b
	};


	@Action("/${:method}")
	public RawData madvocRawImage() {
		return new RawData(SMALLEST_GIF, MimeTypes.lookupMimeType("gif"));
	}

	@Action("/${:method}")
	public String madvocEncoding() {
		return "text:this text contents chinese chars 中文";
	}

}