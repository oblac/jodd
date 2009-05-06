// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx;

/**
 * Transaction statuses.
 */
public enum JtxStatus {

	/**
	 * A transaction is associated with the target object and it is in the active state.
	 */
	STATUS_ACTIVE(0),

	/**
     * A transaction is associated with the target object and it has been marked for rollback,
	 * perhaps as a result of a <code>setRollbackOnly</code> operation.
     */
    STATUS_MARKED_ROLLBACK(1),

	/**
	 * NOT IN USE IN JTX, since two-phase protocol is not supported.
     */
	//STATUS_PREPARED(2),

	/**
	 * A transaction is associated with the target object and it has been committed.
	 */
	STATUS_COMMITTED(3),

	/**
	 * A transaction is associated with the target object and the outcome has been determined as rollback.
	 */
	STATUS_ROLLEDBACK(4),

	/**
     * A transaction is associated with the target object but its status is unknown.
     */
	STATUS_UNKNOWN(5),

	/**
	 * No transaction is currently associated with the target object. The auto-commit mode or the transaction
	 * has been completed.
	 */
	STATUS_NO_TRANSACTION(6),

	/**
	 * NOT IN USE IN JTX, since two-phase protocol is not supported.
	 */
	//STATUS_PREPARING(7),
	
	/**
	 * A transaction is associated with the target object and it is in the process of committing.
	 */
	STATUS_COMMITTING(8),

	/**
	 * A transaction is associated with the target object and it is in the process of rolling back.
	 */
	STATUS_ROLLING_BACK(9);
	

	private int value;

	JtxStatus(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	@Override
	public String toString() {
		switch(value) {
			case 0: return "Active";
			case 1: return "Marked for rollback";
			case 3: return "Committed";
			case 4: return "Rolled back";
			case 5: return "Unknown";
			case 6: return "No transaction";
			case 8: return "Committing";
			case 9: return "Rolling back";
			default: return "Undefined";
		}
	}

}
