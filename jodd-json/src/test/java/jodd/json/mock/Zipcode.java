// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock;

public class Zipcode {
	private String zipcode;

	public Zipcode() {
	}

	public Zipcode(String zipcode) {
		this.zipcode = zipcode;
	}


	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Zipcode zipcode1 = (Zipcode) o;

		if (zipcode != null ? !zipcode.equals(zipcode1.zipcode) : zipcode1.zipcode != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return zipcode != null ? zipcode.hashCode() : 0;
	}
}
