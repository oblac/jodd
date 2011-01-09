// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta;

import examples.proxetta.log.Log;

/**
 * Some class that will be proxied.
 */
@Custom
@Log
public class Foo {

	public Foo() {
		System.out.println("Foo.Foo");
	}

	@Log(value="zzz", broj=3)
	public Foo(int i) {
		System.out.println("Foo.Foo(" + i + ')');
	}


	@Custom
	@Log
	public Foo(@Custom String s) {
		System.out.println("Foo.Foo(" + s + ')');
	}

	@Custom
	public Foo(String s, int i) {
		System.out.println("Foo.Foo(" + s + ", " + i + ')');
	}


	@Custom
	public String one(Integer i) {
		System.out.println("one");
		return i.toString();
	}

	@Log(value="zoro", broj = 2)
	public void two() {
		System.out.println("two");
	}

	@Custom @Log
	public void three() {
		System.out.println("three");
	}

	@Log
	public String four(String s, int i2) {
		System.out.println("four");
		return "xxx";
	}

}
