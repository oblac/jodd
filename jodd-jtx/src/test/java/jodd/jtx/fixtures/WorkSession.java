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

package jodd.jtx.fixtures;

import jodd.exception.UncheckedException;

/**
 * Transactional resource, encapsulates working session.
 */
public class WorkSession {

	static String persistedValue = "The big bang theory";

	public static String getPersistedValue() {
		return persistedValue;
	}

	public WorkSession() {
	}

	public WorkSession(int txno) {
		this.txno = txno;
	}

	String sessionValue;
	boolean readOnly;
	int txno;

	public void writeValue(String value) {
		if (txno == 0) {	// no transaction
			persistedValue = value;
			return;
		}
		// under transaction
		if (readOnly) {
			throw new UncheckedException();
		}
		sessionValue = "[" + txno + "] " + value;
	}

	public String readValue() {
		if (sessionValue != null) {
			return sessionValue;
		}
		return persistedValue;
	}

	// aka commit
	public void done() {
		if (sessionValue != null) {
			persistedValue = sessionValue;
		}
		sessionValue = null;
	}

	// aka rollback
	public void back() {
		sessionValue = null;
	}
}
