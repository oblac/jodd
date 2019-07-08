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

import java.util.Objects;

import static jodd.jtx.JtxIsolationLevel.ISOLATION_DEFAULT;
import static jodd.jtx.JtxPropagationBehavior.PROPAGATION_SUPPORTS;

/**
 * Transaction mode is defined by {@link JtxPropagationBehavior propagation behavior},
 * {@link JtxIsolationLevel isolation level} and read-only flag.
 */
public class JtxTransactionMode {

	public JtxTransactionMode(
			final JtxPropagationBehavior propagationBehavior,
			final JtxIsolationLevel isolationLevel,
			final boolean readOnly,
			final int timeout) {
		this.propagationBehavior = propagationBehavior;
		this.isolationLevel = isolationLevel;
		this.readOnlyMode = readOnly;
		this.timeout = timeout;
	}
	public JtxTransactionMode(
			final JtxPropagationBehavior propagationBehavior,
			final boolean readOnly) {
		this.propagationBehavior = propagationBehavior;
		this.isolationLevel = JtxIsolationLevel.ISOLATION_DEFAULT;
		this.readOnlyMode = readOnly;
		this.timeout = DEFAULT_TIMEOUT;
	}

	// ---------------------------------------------------------------- propagation behaviour

	protected final JtxPropagationBehavior propagationBehavior;

	public JtxPropagationBehavior getPropagationBehavior() {
		return propagationBehavior;
	}

	// ---------------------------------------------------------------- isolation

	private final JtxIsolationLevel isolationLevel;

	public JtxIsolationLevel getIsolationLevel() {
		return isolationLevel;
	}

	// ---------------------------------------------------------------- read-only

	public static final boolean READ_ONLY		= true;
	public static final boolean READ_WRITE		= false;

	private final boolean readOnlyMode;

	public boolean isReadOnly() {
		return readOnlyMode;
	}

	// ---------------------------------------------------------------- time-out

	public static final int DEFAULT_TIMEOUT = -1;

	private final int timeout;

	/**
	 * Returns transaction timeout in seconds.
	 */
	public int getTransactionTimeout() {
		return timeout;
	}

	// ---------------------------------------------------------------- equals & hashCode

	@Override
	public boolean equals(final Object object) {
		if (this == object) {
			return true;
		}
		if (this.getClass() != object.getClass()) {
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
		return Objects.hash(propagationBehavior, readOnlyMode, isolationLevel, timeout);
	}

	// ---------------------------------------------------------------- defaults

	public static final JtxTransactionMode PROPAGATION_SUPPORTS_READ_ONLY = new JtxTransactionMode(PROPAGATION_SUPPORTS, ISOLATION_DEFAULT, READ_ONLY, DEFAULT_TIMEOUT);


	// ---------------------------------------------------------------- to string

	/**
	 * Returns tx description for debugging purposes.
	 */
	@Override
	public String toString() {
		return "jtx{" + propagationBehavior +
			',' + (readOnlyMode ? "readonly" : "readwrite") +
			',' + isolationLevel.toString() + ',' +
			timeout + '}';
	}
}
