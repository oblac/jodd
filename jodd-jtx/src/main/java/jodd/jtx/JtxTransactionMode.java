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

import jodd.util.HashCode;
import static jodd.util.HashCode.hash;
import static jodd.jtx.JtxPropagationBehavior.*;
import static jodd.jtx.JtxIsolationLevel.*;

/**
 * Transaction mode is defined by {@link JtxPropagationBehavior propagation behavior},
 * {@link JtxIsolationLevel isolation level} and read-only flag.
 */
public class JtxTransactionMode {

	public JtxTransactionMode() {
		this.propagationBehavior = PROPAGATION_SUPPORTS;
		this.isolationLevel = ISOLATION_DEFAULT;
		this.readOnlyMode = READ_ONLY;
		this.timeout = DEFAULT_TIMEOUT;
	}

	// ---------------------------------------------------------------- propagation behaviour

	protected JtxPropagationBehavior propagationBehavior;

	public JtxPropagationBehavior getPropagationBehavior() {
		return propagationBehavior;
	}

	/**
	 * Specifies new propagation behaviour.
s	 */
	public void setPropagationBehaviour(JtxPropagationBehavior propagation) {
		this.propagationBehavior = propagation;
	}

	/**
	 * Propagation required.
	 * <pre>{@code
	 * None -> T2 (same session, new tx)
	 * T1 -> T1 (same session, join tx)
	 * }</pre>
	 */
	public JtxTransactionMode propagationRequired() {
		this.propagationBehavior = PROPAGATION_REQUIRED;
		return this;
	}

	/**
	 * Propagation supports.
	 * <pre>{@code
	 * None -> None (same session)
	 * T1 -> T1 (same session, join tx)
	 * }</pre>
	 */
	public JtxTransactionMode propagationSupports() {
		this.propagationBehavior = PROPAGATION_SUPPORTS;
		return this;
	}

	/**
	 * Propagation mandatory.
	 * <pre>{@code
	 * None -> Error
	 * T1 -> T1 (same session, join tx)
	 * }</pre>
	 */
	public JtxTransactionMode propagationMandatory() {
		this.propagationBehavior = PROPAGATION_MANDATORY;
		return this;
	}

	/**
	 * Propagation requires new.
	 * <pre>{@code
	 * None -> T2 (same session, new tx)
	 * T1 -> T2  (new session, new tx)
	 * }</pre>
	 */
	public JtxTransactionMode propagationRequiresNew() {
		this.propagationBehavior = PROPAGATION_REQUIRES_NEW;
		return this;
	}

	/**
	 * Propagation not supported.
	 * <pre>{@code
	 * None -> None (same session)
	 * T1 -> None (new session, no tx)
	 * }</pre>
	 */
	public JtxTransactionMode propagationNotSupported() {
		this.propagationBehavior = PROPAGATION_NOT_SUPPORTED;
		return this;
	}
	/**
	 * Propagation never.
	 * <pre>{@code
	 * None -> None (same session)
	 * T1 -> Error
	 * }</pre>
	 */
	public JtxTransactionMode propagationNever() {
		this.propagationBehavior = PROPAGATION_NEVER;
		return this;
	}


	// ---------------------------------------------------------------- isolation

	private JtxIsolationLevel isolationLevel;

	public JtxIsolationLevel getIsolationLevel() {
		return isolationLevel;
	}

	public void setIsolationLevel(JtxIsolationLevel isolation) {
		this.isolationLevel = isolation;
	}

	public JtxTransactionMode isolationNone() {
		this.isolationLevel = ISOLATION_NONE;
		return this;
	}
	public JtxTransactionMode isolationReadUncommitted() {
		this.isolationLevel = ISOLATION_READ_UNCOMMITTED;
		return this;
	}
	public JtxTransactionMode isolationReadCommitted() {
		this.isolationLevel = ISOLATION_READ_COMMITTED;
		return this;
	}
	public JtxTransactionMode isolationRepeatableRead() {
		this.isolationLevel = ISOLATION_REPEATABLE_READ;
		return this;
	}
	public JtxTransactionMode isolationSerializable() {
		this.isolationLevel = ISOLATION_SERIALIZABLE;
		return this;
	}



	// ---------------------------------------------------------------- read-only

	public static final boolean READ_ONLY		= true;
	public static final boolean READ_WRITE		= false;

	private boolean readOnlyMode;

	public boolean isReadOnly() {
		return readOnlyMode;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnlyMode = readOnly;
	}

	public JtxTransactionMode readOnly(boolean readOnly) {
		this.readOnlyMode = readOnly;
		return this;
	}

	// ---------------------------------------------------------------- time-out

	public static final int DEFAULT_TIMEOUT = -1;

	private int timeout;

	/**
	 * Returns transaction timeout in seconds.
	 */
	public int getTransactionTimeout() {
		return timeout;
	}

	/**
	 * Sets transaction timeout in seconds.
	 */
	public void setTransactionTimeout(int timeout) {
		if (timeout < DEFAULT_TIMEOUT) {
			throw new JtxException("Invalid TX timeout: " + timeout);
		}
		this.timeout = timeout;
	}

	public JtxTransactionMode transactionTimeout(int timeout) {
		setTransactionTimeout(timeout);
		return this;
	}

	// ---------------------------------------------------------------- equals & hashCode

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof JtxTransactionMode)) {
			return false;
		}
		JtxTransactionMode mode = (JtxTransactionMode) object;
		return  (mode.getPropagationBehavior() == this.propagationBehavior) &&
				(mode.getIsolationLevel() == this.isolationLevel) &&
				(mode.isReadOnly() == this.readOnlyMode) &&
				(mode.getTransactionTimeout() == this.timeout);
	}

	@Override
	public int hashCode() {
		int result = HashCode.SEED;
		result = hash(result, propagationBehavior);
		result = hash(result, readOnlyMode);
		result = hash(result, isolationLevel);
		result = hash(result, timeout);
		return result;
	}


	// ---------------------------------------------------------------- to string

	/**
	 * Returns tx description for debugging purposes.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("jtx{");
		sb.append(propagationBehavior.toString());
		sb.append(',').append(readOnlyMode ? "readonly" : "readwrite");
		sb.append(',').append(isolationLevel.toString());
		sb.append(',').append(timeout).append('}');
		return sb.toString();
	}
}
