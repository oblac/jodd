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
import jodd.madvoc.action.ArgsAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class ArgsTestBase {

	@Test
	public void testArgs() {
		HttpResponse response;
		response = HttpRequest.get("localhost:8173/args.hello.html?id=1").send();

		assertEquals("+ mad 1voc + jodd 1", response.bodyText().trim());
	}

	@Test
	public void testArgs2() {
		ArgsAction.User.counter = 0;
		HttpResponse response;
		response = HttpRequest.get("localhost:8173/args.world.html")
				.query("who", "me")
				.query("name", "Jupiter")
				.query("hello.id", "1")
				.query("id", "3")
				.query("muti", "7")
				.send();

		assertEquals("**me+Jupiter+1+3**Jupiter**bye-true-7**8**jojo", response.bodyText().trim());
	}

	@Test
	public void testArgs3() {
		ArgsAction.User.counter = 0;
		HttpResponse response;
		response = HttpRequest.get("localhost:8173/args.user.html")
				.query("user.id", "3")
				.query("user.username", "Frank")
				.send();

		assertEquals("Hello Frank, you are number 3 or 1.", response.bodyText().trim());
	}

}
