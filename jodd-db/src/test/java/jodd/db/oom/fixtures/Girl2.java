// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.db.oom.fixtures;

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
