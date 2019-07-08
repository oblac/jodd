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

package jodd.proxetta.fixtures.inv;

import jodd.proxetta.fixtures.data.Action;
import jodd.proxetta.fixtures.data.InterceptedBy;
import jodd.proxetta.fixtures.data.MadvocAction;
import jodd.proxetta.fixtures.data.PetiteBean;

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

