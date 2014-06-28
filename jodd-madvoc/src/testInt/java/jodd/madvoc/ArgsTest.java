// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.madvoc.action.ArgsAction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArgsTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocSuite.stopTomcat();
	}

	@Test
	public void testArgs() {
		HttpResponse response;
		response = HttpRequest.get("localhost:8080/args.hello.html?id=1").send();

		assertEquals("+ mad 1voc + jodd 1", response.bodyText().trim());
	}

	@Test
	public void testArgs2() {
		ArgsAction.User.counter = 0;
		HttpResponse response;
		response = HttpRequest.get("localhost:8080/args.world.html")
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
		response = HttpRequest.get("localhost:8080/args.user.html")
				.query("user.id", "3")
				.query("user.username", "Frank")
				.send();

		assertEquals("Hello Frank, you are number 3 or 1.", response.bodyText().trim());
	}

}