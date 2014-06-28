// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.ScopeType;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;
import jodd.mutable.MutableInteger;

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

		public void setId(long id) {
			this.id = id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
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
	public void hello(Hello hello, Data2 data) {
		name = "mad " + hello.id;
		hello.out = "voc";
		data.value = "jodd " + data.id;
	}

	@Action
	public void world(
			@In @Out("ime") String name,
			@InOut MutableInteger muti,
			@In("hello") Data2 hello,
			Hello hello2,
			@In(scope = ScopeType.SERVLET)HttpServletRequest request,
			@Out User user
			) {

		hello2.out = "bye-" + (request != null) + "-" + muti.intValue();

		muti.value++;

		user.id = 7;
		user.username = "jojo";

		this.name = who + "+" + name + "+" + hello.id + "+" + hello2.id;
	}

	@Action
	public void user(@InOut User user) {
	}

}