// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.io.Serializable;

public class TFooBean implements Serializable, TFooIndyEx {

	private static final long serialVersionUID = 3689908457570776112L;

	public String getPublic() { 
		return "public";
	}
	
	String getDefault() { 
		return "default";
	}
	
	protected String getProtected() { 
		return "protected";
	}
	
	private String getPrivate() { 
		return "private";
	}
	
	public String getMore(String s, Integer i) {
		return s + i;
	}
}
