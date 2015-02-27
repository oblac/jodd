// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock.superhero;

public class SecretIdentity {

	private String name;

	protected SecretIdentity() {
	}

	public SecretIdentity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SecretIdentity that = (SecretIdentity) o;

		if (name != null ? !name.equals(that.name) : that.name != null) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return (name != null ? name.hashCode() : 0);
	}
}
