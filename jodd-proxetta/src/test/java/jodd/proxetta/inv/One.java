// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.inv;

import jodd.proxetta.data.Action;
import jodd.proxetta.data.InterceptedBy;
import jodd.proxetta.data.MadvocAction;
import jodd.proxetta.data.PetiteBean;

import java.io.Serializable;

@MadvocAction(value = "madvocAction")
@PetiteBean(value = "petiteBean")
@InterceptedBy({One.class, Two.class})
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

