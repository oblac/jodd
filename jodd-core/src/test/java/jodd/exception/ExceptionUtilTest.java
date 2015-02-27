// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.exception;

import org.junit.Test;

import java.io.IOException;

import static jodd.exception.ExceptionUtil.getExceptionChain;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class ExceptionUtilTest {

	@Test
	public void testCurrentStackTrace() {
		StackTraceElement[] sts =ExceptionUtil.getCurrentStackTrace();

		StackTraceElement st = sts[0];

		assertEquals(this.getClass().getName(), st.getClassName());
		assertEquals("testCurrentStackTrace", st.getMethodName());
	}

	@Test
	public void testExceptionChain() {
		try {
			throwTwoExceptions();
			fail();
		} catch (Exception ex) {
			Throwable[] ts = getExceptionChain(ex);

			assertEquals(2, ts.length);
			assertEquals(IllegalArgumentException.class, ts[0].getClass());
			assertEquals(NullPointerException.class, ts[1].getClass());

			assertEquals(NullPointerException.class, ExceptionUtil.getRootCause(ex).getClass());

			assertNotNull(ExceptionUtil.findCause(ex, NullPointerException.class));
			assertNotNull(ExceptionUtil.findCause(ex, IllegalArgumentException.class));
			assertNull(ExceptionUtil.findCause(ex, IndexOutOfBoundsException.class));
		}
	}

	@Test
	public void testThrowChecked() {
		try {
			throwMe();
			fail();
		} catch (Exception ex) {
			assertEquals(IOException.class, ex.getClass());
		}
	}

	public void throwTwoExceptions() {
		throw new IllegalArgumentException(new NullPointerException());
	}

	public void throwMe() {
		ExceptionUtil.throwException(new IOException());
	}

}