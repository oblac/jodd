// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.test;

import jodd.petite.meta.PetiteInject;
import jodd.petite.meta.PetiteInitMethod;

import java.util.List;
import java.util.ArrayList;

public class Boo {

	public List<String> orders = new ArrayList<String>();

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

	public int getCount() {
		return count;
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


	@PetiteInject
	public final Zoo zoo = null;

}
