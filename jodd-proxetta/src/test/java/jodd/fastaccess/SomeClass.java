package jodd.fastaccess;

import jodd.datetime.JDateTime;
import jodd.mutable.MutableInteger;

public class SomeClass {

	public String field1 = "jodd.org";
	public int field2 = 2;
	public long field3 = 3;
	public float field4 = 4;
	public double field5 = 5;
	public byte field6 = 6;
	public short field7 = 7;
	public boolean field8 = true;


	public String method0() {
		return "jodd0";
	}
	public String method1(String s1) {
		return s1;
	}

	public String method2(String string, Integer number) {
		return string + number;
	}

	public String methodBig(String s1, int i2, Double d3, double d4, Integer i5, JDateTime jdt6, MutableInteger mi7, byte b8) {
		return s1 + i2 + d3 + d4 + i5 + jdt6 + mi7 + b8;
	}

	public void methodNone() {
	}

	public int methodInt(int i1, int i2) {
		return i1 + i2;
	}

	public String getJuice() {
		return "orange";
	}

	public void set(Object value) {
		field1 = (String) value;
	}

}