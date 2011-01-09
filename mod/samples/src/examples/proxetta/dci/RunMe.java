// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.dci;

import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;

/**
 * Example.
 */
public class RunMe {

	public static void main(String[] args) throws Exception {

		// Since Proxetta runs in runtime, you can't simply instantiate desired object.
		// Instead you need to have some factory for roles,
		// that will be able to modify the class before it returns it instance.
		// So, instead of new MyRole() we will call createRole() factory method.
		MyRole myRole = createRole(MyRole.class);

		// do the stuff
		myRole.doStuffInContext();
	}

	/**
	 * Factory method for the roles. It makes a proxy for a role and returns a new instance.
	 * This is a good place where we can cache instances, or to introduce a DI container
	 * (such Petite from Jodd :)
	 */
	private static <T> T createRole(Class<T> target) throws Exception {
		Class<T> proxified = Proxetta.withAspects(aspect).defineProxy(target);
		return proxified.newInstance();
	}

	/**
	 * This aspect defines where we wanna insert the changes and what are the modifications.
	 */
	private static ProxyAspect aspect = new ProxyAspect(RoleAdvice.class, new RolePointcut());

}
