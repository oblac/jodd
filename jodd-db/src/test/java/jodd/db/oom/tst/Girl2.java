// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.tst;

import jodd.db.oom.meta.DbTable;
import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;

import java.sql.Timestamp;
import java.util.List;

@DbTable("GIRL")
public class Girl2 {

	public Girl2() {
	}

	public Girl2(int id, String name, String speciality) {
		this.id = Integer.valueOf(id);
		this.name = name;
		this.speciality = speciality;
	}

	public Girl2(String name) {
		this.name = name;
	}

	@DbId
	public Integer id;

	@DbColumn
	public String speciality;

	@DbColumn
	public String name;

	@DbColumn
	public Timestamp time;


		// ---------------------------------------------------------------- equals hashCode

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Girl2 girl = (Girl2) o;

		if (id != girl.id) return false;
		if (name != null ? !name.equals(girl.name) : girl.name != null) return false;
		if (speciality != null ? !speciality.equals(girl.speciality) : girl.speciality != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = speciality != null ? speciality.hashCode() : 0;
		result = 31 * result + id;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	// ---------------------------------------------------------------- boys

	List<Boy> boys;

	public List<Boy> getBoys() {
		return boys;
	}

	public void setBoys(List<Boy> boys) {
		this.boys = boys;
	}



}
