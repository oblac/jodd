// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.proxetta.fixtures.data;

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
