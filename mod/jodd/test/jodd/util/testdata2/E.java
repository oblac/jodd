// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.testdata2;

public class E extends D {
	public E() {
		setPrivate();
		setDefault();
		setProtected();
		setPublic();
	}

	private int pprivate;
	private void setPrivate() {}

	int pdefault;
	void setDefault() {}

	protected int pprotected;
	@Override
	protected void setProtected() {}

	public int ppublic;
	@Override
	public void setPublic() {}

}
