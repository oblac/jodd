// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.n.proxy.advice;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import static jodd.proxetta.ProxyTarget.*;

public class JoAdvice implements ProxyAdvice {

	Integer nnn = new Integer(333);
	static Integer mmm = new Integer(999);

	public JoAdvice() {
		inc2();
	}
	{
		inc3();
	}

	public static void inc() {
		mmm = new Integer(222);
	}

	public static void inc3() {
		mmm = new Integer(999);
	}


	public void inc2() {
		nnn = new Integer(444);
	}

	public Object execute() throws Exception {
		inc2();
		System.out.println("nnn = " + nnn);
		inc();
		System.out.println("mmm = " + mmm);

/*
		Class c = argumentType(1);
		System.out.println(c);

		Object arg1 = argument(1);
		System.out.println("arg " + arg1);

		Object arg2 = argument(2);
		System.out.println("arg " + arg2);

		System.out.println(target());
*/

/*
		try {
			Object o = invoke();
			System.out.println(o);
			return o;
		} catch (Exception ex) {
			throw ex;
		}
*/
		System.out.println(ProxyTarget.argumentType(1));
//		System.out.println(ProxyTarget.argumentType(2));
//		System.out.println(ProxyTarget.argumentType(3));
		System.out.println("-->R " + ProxyTarget.returnType());
		return invoke();
	}

}
