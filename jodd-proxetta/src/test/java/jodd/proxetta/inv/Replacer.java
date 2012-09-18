// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.inv;

public class Replacer {

	public static int rInvVirtual(Object target, String what) {
		System.out.print("REPLACED VIRTUAL! " + target.getClass().getName() + " * " + what + '!');
		return 173;
	}

	public static int rInvStatic(String what, String owner, String methodName, String methodSignature, Class targetClass, Object clone) {
		System.out.print("REPLACED STATIC! " + what + " * " + owner + " * " + methodName + " * " + methodSignature + " * " + targetClass.getName() + " * " + clone.getClass().getName() + '!');
		return 137;
	}

	public static void rInvInterface(Object implementation, String what) {
		System.out.print("REPLACED INTERFACE! " + implementation + " * " + what + '!');
	}

	public static Two rInvNew() {
		return new Two();
	}

	public static Two rInvNew(String state) {
		return new Two("REPLACED " + state);
	}
}
