// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

import java.util.List;

public class Str {

	public String foo() {
		System.out.println("Str.foo");
		return "123";
	}
	public String foo2(List in1, Thread in2) {
		System.out.println("Str.foo2");
		return "123";
	}

	public Integer boo() {
		System.out.println("Str.boo");
		return Integer.valueOf(123);
	}

	public int izoo() {
		System.out.println("Str.izoo");
		return 345;
	}
	public float fzoo() {
		System.out.println("Str.fzoo");
		return 345;
	}
	public double dzoo() {
		System.out.println("Str.dzoo");
		return 345;
	}
	public long lzoo() {
		System.out.println("Str.lzoo");
		return 345;
	}
	public boolean bzoo() {
		System.out.println("Str.bzoo");
		return true;
	}
	public short szoo() {
		System.out.println("Str.bzoo");
		return 1;
	}
	public char czoo() {
		System.out.println("Str.czoo");
		return 'a';
	}
	public byte yzoo() {
		System.out.println("Str.yzoo");
		return 1;
	}

	public void voo() {
		System.out.println("Str.voo");
	}
}
