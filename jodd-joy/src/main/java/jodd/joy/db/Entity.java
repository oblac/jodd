// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.util.HashCode;

import static jodd.util.HashCode.SEED;

/**
 * Abstract entity. It may be used when <code>@DbId</code> annotation is not used.
 */
public abstract class Entity implements DbEntity {

	/**
	 * {@inheritDoc}
	 */
	public boolean isPersistent() {
		return getEntityId() != 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public void detach() {
		setEntityId(0);
	}

	// ---------------------------------------------------------------- equals

	@Override
	public int hashCode() {
		int hash = SEED;
		hash = HashCode.hash(hash, getEntityId());
		hash = HashCode.hash(hash, getClass());
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o.getClass() != this.getClass()) {
			return false;
		}
		Entity entity = (Entity) o;

		if (getEntityId() == 0 && entity.getEntityId() == 0) {
			return this == o;
		}
		return getEntityId() == entity.getEntityId();
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return "Entity{" + this.getClass().getSimpleName() + ':' + getEntityId() + '}';
	}

}