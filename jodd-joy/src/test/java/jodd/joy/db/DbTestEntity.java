// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.db.oom.meta.DbId;

public abstract class DbTestEntity extends Entity {

	@DbId
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getEntityId() {
		if (id == null) {
			return 0;
		}
		return id.longValue();
	}

	public void setEntityId(long id) {
		if (id == 0) {
			this.id = null;
		} else {
			this.id = Long.valueOf(id);
		}
	}
}