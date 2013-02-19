// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import org.junit.Assert;
import org.junit.Test;

public class HttpUtilTest {

	@Test
	public void testNiceHeaderNames() {
		Assert.assertEquals("Content-Type", HttpUtil.prepareHeaderParameterName("conTent-tyPe"));
		Assert.assertEquals("ETag", HttpUtil.prepareHeaderParameterName("etag"));
	}
}
