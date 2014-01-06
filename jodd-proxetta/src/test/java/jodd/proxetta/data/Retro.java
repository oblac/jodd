// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

public class Retro {

	public boolean flag;

	public String method1() {
		return flag ? "retro" : "RETRO";
	}

	public int method2() {
		return flag ? 2 : -2;
	}

	public long method3() {
		return flag ? 3 : -3;
	}

	public short method4() {
		return (short) (flag ? 4 : -4);
	}

	public byte method5() {
		return (byte) (flag ? 5 : -5);
	}

	public boolean method6() {
		return flag;
	}

	public float method7() {
		return (float) (flag ? 7.7 : -7.7);
	}

	public double method8() {
		return (flag ? 8.8 : -8.8);
	}

	public int[] method9() {
		return (flag ? new int[9] : new int[1]);
	}

	public void method10() {
	}

	public char method11() {
		return flag ? 'r' : 'R';
	}

}
