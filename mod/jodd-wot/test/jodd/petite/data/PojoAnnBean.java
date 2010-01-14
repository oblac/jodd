// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.data;

import jodd.petite.meta.PetiteInject;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.meta.PetiteBean;

@PetiteBean("pojo")
public class PojoAnnBean {

	public final SomeService fservice;

	@PetiteInject
	public PojoAnnBean(SomeService fservice) {
		this.fservice = fservice;
	}

	@PetiteInject("someService")
	public SomeService service;

	public SomeService service2;

	@PetiteInject("someService")
	public void injectService(SomeService service) {
		service2 = service;
	}

	public int count;

	@PetiteInitMethod
	public void init() {
		count++;
	}

}
