// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

import jodd.util.HashCode;

import static jodd.util.HashCode.SEED;

/**
 * Abstract entity.
 */
public abstract class Entity {

	/**
	 * Returns entity ID. Value 0 means that entity
	 * is not stored in the persistence layer.
	 */
	protected abstract long getEntityId();

	/**
	 * Sets entity ID.
	 */
	protected abstract void setEntityId(long id);

	/**
	 * Returns <code>true</code> if entity is persisted, i.e.
	 * {@link #getEntityId() ID} is not <code>0</code>
	 */
	public boolean isPersistent() {
		return getEntityId() != 0;
	}

	/**
	 * Detaches entity by setting ID to <code>0</code>.
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