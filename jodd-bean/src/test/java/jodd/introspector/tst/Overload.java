// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector.tst;

public class Overload {

	String company;

	// not a property setter
	public void setCompany(StringBuilder sb) {
		this.company = sb.toString();
	}

	public String getCompany() {
		return company;
	}
}