package jodd.fastaccess;

import jodd.datetime.JDateTime;
import jodd.mutable.MutableInteger;

public class SomeClass {

	public String method0() {
		return "jodd0";
	}
	public String method1(String s1) {
		return s1;
	}

	public String method2(String string, Integer number) {
		return string + number;
	}

	public String methodBig(String s1, int i2, Double d3, double d4, Integer i5, JDateTime jdt6, MutableInteger mi7) {
		return s1 + i2 + d3 + d4 + i5 + jdt6 + mi7;
	}

	public void methodNone() {
	}

	public int methodInt(int i1, int i2) {
		return i1 + i2;
	}

	public String getJuice() {
		return "orange";
	}

	public Object invoke(Object destination, Object[] args) {
		return methodInt((Integer)args[0], (Integer)args[1]);
	}

}