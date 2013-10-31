package jodd.directaccess;

import jodd.datetime.JDateTime;
import jodd.mutable.MutableInteger;

public class SomeClass {

	// ---------------------------------------------------------------- fields

	public String field1 = "jodd.org";
	public int field2 = 2;
	public long field3 = 3;
	public float field4 = 4;
	public double field5 = 5;
	public byte field6 = 6;
	public short field7 = 7;
	public boolean field8 = true;

	// ---------------------------------------------------------------- methods

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

	public void invoke(Object value) {
		setProp1((Integer) value);
	}

	// ---------------------------------------------------------------- bean

	private String prop0;
	private int prop1;
	private long prop2;
	private float prop3;
	private double prop4;
	private short prop5;
	private byte prop6;
	private boolean prop7;
	private char prop8;

	public String getProp0() {
		return prop0;
	}

	public void setProp0(String prop0) {
		this.prop0 = prop0;
	}

	public int getProp1() {
		return prop1;
	}

	public int setProp1(int prop1) {
		this.prop1 = prop1;
		return 2;
	}

	public long getProp2() {
		return prop2;
	}

	public void setProp2(long prop2) {
		this.prop2 = prop2;
	}

	public float getProp3() {
		return prop3;
	}

	public void setProp3(float prop3) {
		this.prop3 = prop3;
	}

	public double getProp4() {
		return prop4;
	}

	public void setProp4(double prop4) {
		this.prop4 = prop4;
	}

	public short getProp5() {
		return prop5;
	}

	public void setProp5(short prop5) {
		this.prop5 = prop5;
	}

	public byte getProp6() {
		return prop6;
	}

	public void setProp6(byte prop6) {
		this.prop6 = prop6;
	}

	public boolean isProp7() {
		return prop7;
	}

	public void setProp7(boolean prop7) {
		this.prop7 = prop7;
	}

	public char getProp8() {
		return prop8;
	}

	public void setProp8(char prop8) {
		this.prop8 = prop8;
	}

}