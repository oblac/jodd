// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.result.RawDownload;
import jodd.madvoc.result.RawResultData;
import jodd.servlet.ServletUtil;

@MadvocAction
public class DownloadAction {

	@Action(extension = Action.NONE)
	public RawResultData execute() {
		String fileContent = "this is download file from jodd.org!";
		return new RawDownload(fileContent.getBytes(), "jodd-download.txt");
	}
}
