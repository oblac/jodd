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
import jodd.madvoc.meta.scope.Session;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@MadvocAction
public class SessAction {

	// need this to keep session id
	@In
	HttpSession httpSession;

	@Out
	String sid;

	// ---------------------------------------------------------------- 1

	class One {
		@Out @Session
		String sessionName;
	}

	/**
	 * Stores session name in the session.
	 */
	@Action
	public void view(@In String name, One one) {
		one.sessionName = name;
		sid = httpSession.getId();
	}

	// ---------------------------------------------------------------- 2

	/**
	 * Modifies value in the session (read-write).
	 */
	@Action
	public void two(
			@In(value="sessionName") @Session
			String sessionName, One one)
	{
		one.sessionName = sessionName.toUpperCase();
		sid = httpSession.getId();
	}

	// ---------------------------------------------------------------- 3

	class Three {
		@In @Out("sessionName") @Session
		String foo;
	}

	/**
	 * Clears value from the session.
	 */
	@Action
	public void three(Three three) {
		three.foo = null;
		sid = httpSession.getId();
	}

	// ---------------------------------------------------------------- 4

	@Out
	boolean notexist;

	/**
	 * Checks if session value is removed!
	 */
	@Action
	public void four() {
		notexist = false;
		Enumeration<String> enumeration = httpSession.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String s = enumeration.nextElement();
			if (s.equals("sessionName")) {
				notexist = false;
				return;
			}
		}
		notexist = true;
	}

}