// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Action;
import jodd.madvoc.result.RawResultData;
import jodd.util.CharUtil;

@MadvocAction
public class RawAction {

	@Action
	public RawResultData view() {
		String result = "this is some raw direct result";
		byte[] bytes = CharUtil.toAsciiArray(result.toCharArray());
		return new RawResultData(bytes);
	}

	@Action
	public String text() {
		return "raw:some raw txt";
	}
}
