// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Action;
import jodd.madvoc.result.RawData;
import jodd.util.CharUtil;
import jodd.util.MimeTypes;

@MadvocAction
public class RawAction {

	@Action
	public RawData view() {
		String result = "this is some raw direct result";
		byte[] bytes = CharUtil.toAsciiByteArray(result.toCharArray());
		return new RawData(bytes, MimeTypes.MIME_TEXT_HTML);
	}

	@Action
	public String text() {
		return "raw:some raw txt";
	}
}
