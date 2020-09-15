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

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;
import jodd.madvoc.meta.RenderWith;
import jodd.madvoc.meta.scope.JsonBody;
import jodd.madvoc.result.JsonActionResult;

import javax.servlet.http.HttpServletRequest;

@MadvocAction
public class ArgsAction {

	class Hello {
		@In Integer id;
		@Out String out;
	}

	static class Data2 {
		@In int id;
		@Out String value;
	}

	public static class User {
		long id;
		String username;
		public static int counter;

		public User() {
			counter++;
		}

		public long getId() {
			return id;
		}

		public void setId(final long id) {
			this.id = id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(final String username) {
			this.username = username;
		}

		public int getCounter() {
			return counter;
		}
	}

	@In
	String who;

	@Out
	String name;

	@Action
	public void hello(final Hello hello, final Data2 data) {
		name = "mad " + hello.id;
		hello.out = "voc";
		data.value = "jodd " + data.id;
	}

	@Action
	public void world(
			@In @Out("ime") final String name,
			@In @Out final Integer muti,
			@In("hello") final Data2 hello,
			final Hello hello2,
			@In final HttpServletRequest request,
			@Out final User user
			) {

		hello2.out = "bye-" + (request != null) + "-" + muti;

		user.id = 7;
		user.username = "jojo";

		this.name = who + "+" + name + "+" + hello.id + "+" + hello2.id;
	}

	@Action
	public void user(@In @Out final User user) {
	}

	@Action
	@RenderWith(JsonActionResult.class)
	public User user2(@In @JsonBody final User user) {
		user.setUsername(user.getUsername() + "!");
		return user;
	}

	public void zigzag(@In @Out final int id) {
		System.out.println("ArgsAction.zigzag");
		System.out.println("id = [" + id + "]");
	}

}
