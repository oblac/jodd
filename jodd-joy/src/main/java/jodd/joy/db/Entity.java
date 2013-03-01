// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

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
	public abstract void setEntityId(long id);

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
		long value = getEntityId();
		return (int)(value ^ (value >>> 32));
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