// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.db.oom.meta.DbId;

/**
 * Abstract entity.
 */
public abstract class Entity {

	/**
	 * Unique entity identifier.
	 */
	@DbId
	protected Long id;

	/**
	 * Returns entity id.
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns <code>true</code> if entity is persisted, i.e. id is not <code>null</code>
	 */
	public boolean isPersistent() {
		return id != null;
	}

	/**
	 * Detaches entity by setting id to <code>null</code>.
	 */
	public void detach() {
		id = null;
	}

	// ---------------------------------------------------------------- equals

	@Override
	public int hashCode() {
		if (id == null) {
			return System.identityHashCode(this);
		}
		return 31 * id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof Entity == false) {
			return false;
		}
		Entity entity = (Entity) o;

		if (id == null && entity.id == null) {
			return this == o;
		}
		if ((id == null) || (entity.id == null)) {
			return false;
		}
		return id.equals(entity.id);
	}



	// ---------------------------------------------------------------- tostring

	@Override
	public String toString() {
		return "Entity{" + this.getClass().getSimpleName() + ':' + id +	'}';
	}
}