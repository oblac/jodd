// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.data;

public class PojoBean2 {

	protected String val1;

	protected Integer val2;

	protected PojoBean3 bean = new PojoBean3();

	public String getVal1() {
		return val1;
	}

	public void setVal1(String val1) {
		this.val1 = val1;
	}

	public Integer getVal2() {
		return val2;
	}

	public void setVal2(Integer val2) {
		this.val2 = val2;
	}

	public PojoBean3 getBean() {
		return bean;
	}

	public void setBean(PojoBean3 bean) {
		this.bean = bean;
	}
}
