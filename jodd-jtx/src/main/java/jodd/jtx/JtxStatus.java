// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
	 * No transaction is currently associated with the target object. Indicates the auto-commit mode.
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

	JtxStatus(final int value) {
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
