// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.db;

/**
 * Marker for mapped database entities.
 */
public interface DbEntity {

	/**
	 * Returns entity ID. Value <code>0</code> means that entity
	 * is not stored in the persistence layer.
	 */
	long getEntityId();

	/**
	 * Sets the entity ID.
	 */
	void setEntityId(long id);

	/**
	 * Returns <code>true</code> if entity is persisted. i.e.
	 * {@link #getEntityId() ID} is not <code>0</code>
	 */
	boolean isPersistent();

	/**
	 * Detaches entity by setting ID to <code>0</code>.
	 */
	void detach();

}