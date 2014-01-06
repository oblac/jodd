// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.tst;

public class Boy {

	public Boy() {}

	public Boy(int id, String name, int girlId) {
		this.id = id;
		this.name = name;
		this.girlId = girlId;
	}

	public int id;

	public String name;
	
	public int girlId;

	// ---------------------------------------------------------------- hash equals

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Boy boy = (Boy) o;

		if (girlId != boy.girlId) return false;
		if (id != boy.id) return false;
		if (name != null ? !name.equals(boy.name) : boy.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + girlId;
		return result;
	}
}
