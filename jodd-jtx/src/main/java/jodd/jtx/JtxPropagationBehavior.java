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
 * Transaction propagation behavior values.
 */
public enum JtxPropagationBehavior {

	/**
	 * Support a current transaction, create a new one if none exists.
	 * <pre>{@code
	 * None -> T2 (same session, new tx)
	 * T1 -> T1 (same session, join tx)
	 * }</pre>
	 */
	PROPAGATION_REQUIRED(1),

	/**
	 * Support a current transaction, execute non-transactionally if none exists.
	 * <pre>{@code
	 * None -> None (same session)
	 * T1 -> T1 (same session, join tx)
	 * }</pre>
	 */
	PROPAGATION_SUPPORTS(2),

	/**
	 * Support a current transaction, throw an exception if none exists.
	 * <pre>{@code
	 * None -> Error
	 * T1 -> T1 (same session, join tx)
	 * }</pre>
	 */
	PROPAGATION_MANDATORY(3),

	/**
	 * Create a new transaction, suspend the current transaction if one exists.
	 * <pre>{@code
	 * None -> T2 (same session, new tx)
	 * T1 -> T2  (new session, new tx)
	 * }</pre>
	 */
	PROPAGATION_REQUIRES_NEW(4),

	/**
	 * Execute non-transactionally, suspend the current transaction if one exists.
	 * <pre>{@code
	 * None -> None (same session)
	 * T1 -> None (new session, no tx)
	 * }</pre>
	 */
	PROPAGATION_NOT_SUPPORTED(5),

	/**
	 * Execute non-transactionally, throw an exception if a transaction exists.
	 * <pre>{@code
	 * None -> None (same session)
	 * T1 -> Error
	 * }</pre>
	 */
	PROPAGATION_NEVER(6);

	private int value;

	JtxPropagationBehavior(final int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	@Override
	public String toString() {
		switch(value) {
			case 1: return "Required";
			case 2: return "Supports";
			case 3: return "Mandatory";
			case 4: return "Requires New";
			case 5: return "Not Supported";
			case 6: return "Never";
			default: return "Undefined";
		}
	}
}
