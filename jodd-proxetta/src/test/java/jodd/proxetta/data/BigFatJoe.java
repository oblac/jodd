// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

import java.util.Map;

@MadvocAction(value = "madvocAction")
@PetiteBean(value = "petiteBean")
@InterceptedBy({Str.class, Two.class})
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

	@Override
	@Action(method = "method", extension = "extension", alias = "alias", value = "value")
	@PetiteInject
	@Transaction(readOnly = true, propagation = "PROPAGATION_REQUIRES_NEW")
	public void publicMethod() {
		System.out.println("BigFatJoe.publicMethod");
		super.publicMethod();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public <T> Map<String, T> fullDescription(int i1, String s2, Map<String, T> m3, Class[] arr4) throws RuntimeException {
		return null;
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


	public void runInnerClass() {
		final int counter = StatCounter.counter; 
		new Runnable() {
			public void run() {
				StatCounter.counter = counter + 1;
			}
		}.run();
	}

}
