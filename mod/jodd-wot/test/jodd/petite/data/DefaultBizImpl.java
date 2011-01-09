// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.data;

import jodd.petite.meta.PetiteBean;

@PetiteBean("biz")
public class DefaultBizImpl implements Biz {

	public void calculate() {
		System.out.println("DefaultBizImpl.calculate");
	}
}
