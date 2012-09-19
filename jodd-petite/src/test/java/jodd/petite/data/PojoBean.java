// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.data;

public class PojoBean {

	public final SomeService fservice;

	public PojoBean(SomeService fservice) {
		this.fservice = fservice;
	}

	public SomeService service;

	public SomeService service2;

	public void injectService(SomeService service) {
		service2 = service;
	}

	public int count;

	public void init() {
		count++;
	}

}
