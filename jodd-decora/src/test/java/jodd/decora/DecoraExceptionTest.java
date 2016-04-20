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

package jodd.decora;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.reflect.Whitebox.getInternalState;

import org.junit.Before;
import org.junit.Test;

import jodd.decora.DecoraException;

public class DecoraExceptionTest {

	private Throwable throwableMock;
	private final String TEST_STRING = "TEST";

	@Before
	public void setUp() {
		throwableMock = mock(Throwable.class);
	}

	@Test
	public final void testDecoraExceptionThrowable() {
		// when
		DecoraException decoraException = new DecoraException(throwableMock);

		// then
		assertEquals("Cause field must be set.", throwableMock, getInternalState(decoraException, "cause"));
	}

	@Test
	public final void testDecoraExceptionString() {
		// when
		DecoraException decoraException = new DecoraException(TEST_STRING);

		// then
		assertEquals("DetailMessage field must be set.", TEST_STRING, getInternalState(decoraException, "detailMessage"));
	}

	@Test
	public final void testDecoraExceptionStringThrowable() {
		// when
		DecoraException decoraException = new DecoraException(TEST_STRING, throwableMock);

		// then
		assertEquals("Cause field must be set.", throwableMock, getInternalState(decoraException, "cause"));
		assertEquals("DetailMessage field must be set.", TEST_STRING, getInternalState(decoraException, "detailMessage"));
	}

}
