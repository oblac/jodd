// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

public class One {

	// 1:()V_jodd/proxetta/data/Two#foo
	public void foo() {
	}

	// 1:()Ljava/lang/String;_jodd/proxetta/data/Two#toString
	@Override
	public String toString() {
		return "s";
	}

	public void tata() {
	}


	// 257:()I_jodd/proxetta/data/Two#hashCode
	// 1:(Ljava/lang/Object;)Z_jodd/proxetta/data/Two#equals
}
