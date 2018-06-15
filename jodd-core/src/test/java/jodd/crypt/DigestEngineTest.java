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

package jodd.crypt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DigestEngineTest {

	@Test
	void testSha1() {
		assertEquals("626B0566A836677FA85C6952417E704E727E336C", DigestEngine.sha1().digestString("Jodd"));
	}

	@Test
	void testSha256() {
		assertEquals("D5E94A2DD851E6E2A233EFA00CF26B385A933F26223B00757E189397F8B92530", DigestEngine.sha256().digestString("Jodd"));
	}

	@Test
	void testSha512() {
		assertEquals("ACF65B0C3DE891B2984F461FA12EF4DD205B2DE360F3C834A47368CBDD334687AB5E8405AA910DF8AC6B5631BF1F2CC5133B0D95493A40452EC5B984E4FC31E8", DigestEngine.sha512().digestString("Jodd"));
	}

	@Test
	void testMD5() {
		assertEquals("5513A194A0D3E46B8D90021B283BE791", DigestEngine.md5().digestString("Jodd"));
	}
}
