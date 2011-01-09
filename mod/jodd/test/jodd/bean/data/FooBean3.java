// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;


public class FooBean3 {

	private int pprotected;
	protected int getPprotected() {
		return pprotected;
	}
	protected void setPprotected(int pprotected) {
		this.pprotected = pprotected;
	}


	private int ppackage;
	int getPpackage() {
		return ppackage;
	}
	void setPpackage(int ppackage) {
		this.ppackage = ppackage;
	}


	private int pprivate;
	private int getPprivate() {
		return pprivate;
	}
	private void setPprivate(int pprivate) {
		this.pprivate = pprivate;
	}


}
