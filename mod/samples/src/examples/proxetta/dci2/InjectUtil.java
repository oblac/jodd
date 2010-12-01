// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.dci2;

public class InjectUtil {

	public static Object create(Object role) {
		return FrameworkManager.findRoleTarget(role);
	}
}
