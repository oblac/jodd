// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.methref;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MethrefJava8Test {

	public static interface Foo {

		public void hello(int i);

//		default String hey() {
//			return "HEY";
//		}
	}

	public static class Chuck implements Foo {

		public void hello(int i) {
			System.out.println(i);
		}

		public void nerd() {
			System.out.println("nerd");
		}
	}

	public static class Sara extends Chuck {
		@Override
		public void hello(int i) {
			super.hello(i);
		}
	}

	@Test
	public void testMethrefOnInterfaceWithDefaultMethod() {
		Methref<Sara> methref = Methref.on(Sara.class);

		methref.to().hello(123);
		assertEquals("hello", methref.ref());

		methref.to().nerd();
		assertEquals("nerd", methref.ref());

//		methref.to().hey();
//		assertEquals("hey", methref.ref());
	}
}