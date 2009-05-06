// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.n.proxy;

public class UserServiceProxy2 extends UserService {

	@Override
	public Integer zoo(String[][] doo, long[] a, Integer d) {
		return zoo$0(doo, a, d);
	}

	private Integer zoo$0(String[][] doo, long[] a, Integer d) {
		System.out.println("TX start");
		Object result = super.zoo(doo, a, d);
		System.out.println("TX end ->");
		System.out.println("TX end -> " + result);
		return (Integer) result;
	}
}
