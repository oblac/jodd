// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.biz;

import jodd.petite.meta.PetiteBean;

@PetiteBean
public class FooService {

	public void hello() {
		System.out.println("FooService.hello");
	}
}
