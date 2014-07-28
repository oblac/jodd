// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.ScopeType;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@MadvocAction
public class SessAction {

	// need this to keep session id
	@In(scope = ScopeType.SERVLET)
	HttpSession httpSession;

	@Out
	String sid;

	// ---------------------------------------------------------------- 1

	class One {
		@Out(scope = ScopeType.SESSION)
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
			@In(scope = ScopeType.SESSION, value="sessionName")
			String sessionName, One one)
	{
		one.sessionName = sessionName.toUpperCase();
		sid = httpSession.getId();
	}

	// ---------------------------------------------------------------- 3

	class Three {
		@InOut(scope = ScopeType.SESSION, value = "sessionName")
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