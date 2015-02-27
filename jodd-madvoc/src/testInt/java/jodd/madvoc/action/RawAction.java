// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.result.RawData;
import jodd.madvoc.result.RawDownload;
import jodd.madvoc.result.RawResultData;
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
		return "text:some raw txt";
	}

	@Action(extension = Action.NONE)
	public RawResultData download() {
		String fileContent = "file from jodd.org!";
		return new RawDownload(fileContent.getBytes(), "jodd-download.txt");
	}

}