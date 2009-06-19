// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.interceptor.EchoInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;
import jodd.petite.meta.PetiteInject;
import jodd.jtx.meta.Transaction;
import jodd.jtx.JtxPropagationBehavior;

@MadvocAction(value = "madvocAction")
//@InterceptedBy({EchoInterceptor.class, ServletConfigInterceptor.class})
public class BigFatJoe extends SmallSkinnyZoe {

	@PetiteInject
	public BigFatJoe() {
		System.out.println("BigFatJoe.BigFatJoe");
	}

	static {
		System.out.println("BigFatJoe.static intializer");
	}

	static {
		StatCounter.counter++;
	}

	{
		StatCounter.counter++;
	}

	@Action(method = "method", extension = "extension", alias = "alias", notInPath = true, value = "value")
	@PetiteInject
	@Transaction(readOnly = true, propagation = JtxPropagationBehavior.PROPAGATION_REQUIRES_NEW)
	public void publicMethod() {
		System.out.println("BigFatJoe.publicMethod");
	}

	public void callInnerMethods() {
		System.out.println("BigFatJoe.callInner");
		protectedMethod();
		packageMethod();
		privateMethod();
	}

	public void callInnerMethods2() {
		System.out.println("BigFatJoe.callInnerMethods2");
		superProtectedMethod();
		superPackageMethod();
	}

	protected void protectedMethod() {
		System.out.println("BigFatJoe.protectedMethod");
	}

	void packageMethod() {
		System.out.println("BigFatJoe.packageMethod");
	}

	private void privateMethod() {
		System.out.println("BigFatJoe.privateMethod");
	}

}
