// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpProgressListenerTest {

	@Test
	public void testHttpProgressListener() {
		HttpProgressListener hpl = new HttpProgressListener() {
			@Override
			public void transferred(long len) {

			}
		};

		assertEquals(10, hpl.callbackSize(0));
		assertEquals(10, hpl.callbackSize(1000));
		assertEquals(10, hpl.callbackSize(2000));
		assertEquals(11, hpl.callbackSize(2200));
		assertEquals(12, hpl.callbackSize(2400));
		assertEquals(51, hpl.callbackSize(10240));
		assertEquals(512, hpl.callbackSize(102400));
		assertEquals(512, hpl.callbackSize(102400*2));
	}
}