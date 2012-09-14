// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.testdata;

public class C extends B {

	public C() {
		this.setPrivate();
		this.setProtected();
		this.setDefault();
		this.setPublic();
		
		super.setDefault();
		super.setProtected();
		super.setPublic();
	}

	private int pprivate;
	private void setPrivate() {}

	int pdefault;
	@Override
	void setDefault() {}

	protected int pprotected;
	@Override
	protected void setProtected() {}

	public int ppublic;
	@Override
	public void setPublic() {}

	public int newone;
	public void newOne() {}

}
