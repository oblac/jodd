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

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public abstract class SessionScopeTestBase {

	@Test
	public void testSessionScope() {
		HttpResponse response = HttpRequest.get("localhost:8173/item.html").send();
		String out1 = response.bodyText().trim();

		response = HttpRequest.get("localhost:8173/item.html").send();
		String out2 = response.bodyText().trim();

		assertFalse(out1.equals(out2));

		String jsessionid = out2.substring(out2.indexOf("sid:") + 4);

		response = HttpRequest.get("localhost:8173/item.html;jsessionid=" + jsessionid).send();
		String out3 = response.bodyText().trim();

		assertEquals(out2, out3);
	}

	@Test
	public void testSessionScopeWithScopedProxy() {
		HttpResponse response = HttpRequest.get("localhost:8173/item.global.html").send();
		String out1 = response.bodyText().trim();

		response = HttpRequest.get("localhost:8173/item.global.html").send();
		String out2 = response.bodyText().trim();

		assertFalse(out1.equals(out2));

		String jsessionid = out2.substring(out2.indexOf("sid:") + 4);

		response = HttpRequest.get("localhost:8173/item.global.html;jsessionid=" + jsessionid).send();
		String out3 = response.bodyText().trim();

		assertEquals(out2, out3);
	}

	@Test
	public void testSessionScopeWithInOut() {
		HttpResponse response = HttpRequest.get("localhost:8173/sess.html?name=jodd").send();
		String out = response.bodyText().trim();

		int ndx = out.indexOf('>');
		String sid = out.substring(ndx + 1);
		assertEquals("Sess: jodd", out.substring(0, ndx).trim());

		response = HttpRequest.get("localhost:8173/sess.two.html;jsessionid=" + sid).send();
		out = response.bodyText().trim();

		ndx = out.indexOf('>');
		sid = out.substring(ndx + 1);
		assertEquals("Sess: JODD", out.substring(0, ndx).trim());

		response = HttpRequest.get("localhost:8173/sess.three.html;jsessionid=" + sid).send();
		out = response.bodyText().trim();

		ndx = out.indexOf('>');
		sid = out.substring(ndx + 1);
		assertEquals("Sess:", out.substring(0, ndx).trim());

		response = HttpRequest.get("localhost:8173/sess.four.html;jsessionid=" + sid).send();
		out = response.bodyText().trim();
		assertEquals("ne:true", out.trim());
	}
}
