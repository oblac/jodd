// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static jodd.exception.ExceptionUtil.getExceptionChain;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

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
			fail("error");
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
			fail("error");
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
