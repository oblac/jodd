// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.model;

import jodd.json.meta.JSON;

public class FooBar {

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@JSON(name = "foo.bar")
	private Integer value;

}