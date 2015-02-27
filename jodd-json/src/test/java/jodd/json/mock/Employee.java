// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock;

import java.util.Date;

public class Employee extends Person {

	String company;

	public Employee() {
	}

	public Employee(String firstname, String lastname, Date birthdate, Address home, Address work, String company) {
		super(firstname, lastname, birthdate, home, work);
		this.company = company;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setCompany(StringBuilder builder) {
		this.company = builder.toString();
	}
}
