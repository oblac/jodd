// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.dao;

import jodd.db.oom.meta.DbColumn;

public class BoyCriteria {

	@DbColumn
	Long girlId;

	@DbColumn
	String name;

	public Long getGirlId() {
		return girlId;
	}

	public void setGirlId(Long girlId) {
		this.girlId = girlId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}