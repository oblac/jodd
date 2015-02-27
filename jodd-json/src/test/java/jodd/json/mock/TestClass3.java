// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock;

import org.junit.Ignore;

@Ignore
public class TestClass3 {

	private String name;
	private String category;
	private boolean found = false;

	public TestClass3() {
	}

	public TestClass3(String name, String category, boolean found) {
		this.name = name;
		this.category = category;
		this.found = found;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

	public boolean isFound() {
		return found;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (found ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TestClass3 other = (TestClass3) obj;
		if (found != other.found) {
			return false;
		}
		return true;
	}

}
