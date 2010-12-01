// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.inv;

import jodd.madvoc.interceptor.EchoInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;
import jodd.petite.WiringMode;
import jodd.petite.meta.PetiteBean;

import java.io.Serializable;

@MadvocAction(value = "madvocAction")
@PetiteBean(value = "petiteBean", wiring = WiringMode.OPTIONAL)
@InterceptedBy({EchoInterceptor.class, ServletConfigInterceptor.class})
public class One extends SubOne implements Serializable {

	public One() {
		a = 12;
		Object o = new Object();
		SubOne s = new SubOne();
		System.out.print("one ctor!");
	}

	@Action
	public void example1() {
		Two two = new Two();
		int i = two.invvirtual("one");
		System.out.print(i);
		callSub();
	}

	public void example2() {
		int i = Two.invstatic("one");
		System.out.print(i + ++a);
		System.out.print(a);
		System.out.print("static: " + s);
	}

	public void example3() {
		Two two = new Two("ctor!");
		two.printState();
	}

	public void example4() {
		Three three = new ThreeImpl();
		three.invinterface("four!");
	}

	public void sub() {
		System.out.print(">overriden sub");
	}

	private static int s = 4;
	private int a;

}

