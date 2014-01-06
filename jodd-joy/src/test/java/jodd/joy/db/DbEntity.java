// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.db.oom.meta.DbId;

public abstract class DbEntity extends Entity {

	@DbId
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	protected long getEntityId() {
		if (id == null) {
			return 0;
		}
		return id.longValue();
	}

	@Override
	public void setEntityId(long id) {
		if (id == 0) {
			this.id = null;
		} else {
			this.id = Long.valueOf(id);
		}
	}
}