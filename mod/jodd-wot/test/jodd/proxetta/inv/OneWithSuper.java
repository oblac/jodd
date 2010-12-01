// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.inv;

public class OneWithSuper extends One {

	@Override
	public void example1() {
		Two two = new Two();
		int i = two.invvirtual("one");
		System.out.print(i);
		super.example1();
	}

}
