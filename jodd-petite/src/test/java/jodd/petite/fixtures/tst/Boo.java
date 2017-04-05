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

package jodd.petite.fixtures.tst;

import jodd.petite.meta.PetiteDestroyMethod;
import jodd.petite.meta.PetiteInject;
import jodd.petite.meta.PetiteInitMethod;

import java.util.List;
import java.util.ArrayList;

public class Boo {

	public List<String> orders = new ArrayList<>();

	@PetiteInject
	private Foo foo;

	public Foo getFoo() {
		return foo;
	}

	public void setFoo(Foo foo) {
		foo.counter++;
		this.foo = foo;
	}

	private int count;
	private int count2;

	public int getCount() {
		return count;
	}
	public int getCount2() {
		return count2;
	}

	@PetiteInitMethod
	void init() {
		count++;
		orders.add("init");
	}

	@PetiteInitMethod(order = 100)
	void third() {
		orders.add("third");
	}

	@PetiteInitMethod(order = -1)
	void last() {
		orders.add("last");
	}

	@PetiteInitMethod(order = -2)
	void beforeLast() {
		orders.add("beforeLast");
	}

	@PetiteInitMethod(order = 1)
	void first() {
		orders.add("first");
	}

	@PetiteInitMethod(order = 2)
	void second() {
		orders.add("second");
	}

	@PetiteDestroyMethod
	void ciao() {
		count2++;
	}
	@PetiteDestroyMethod
	void buy() {
		count2++;
	}

	@PetiteInject
	public final Zoo zoo = null;

}
