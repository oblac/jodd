// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpProgressListenerTest {

	@Test
	public void testHttpProgressListener() {
		HttpProgressListener hpl = new HttpProgressListener() {
			@Override
			public void transferred(int len) {

			}
		};

		assertEquals(512, hpl.callbackSize(0));
		assertEquals(512, hpl.callbackSize(1000));
		assertEquals(512, hpl.callbackSize(51200));
		assertEquals(512, hpl.callbackSize(51201));

		assertEquals(1024, hpl.callbackSize(102400));
		assertEquals(1024, hpl.callbackSize(102401));
		assertEquals(1024, hpl.callbackSize(102449));

		assertEquals(1025, hpl.callbackSize(102450));
		assertEquals(1025, hpl.callbackSize(102499));
		assertEquals(1025, hpl.callbackSize(102500));
	}
}