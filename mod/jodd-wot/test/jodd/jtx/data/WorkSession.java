// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.data;

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
		if (readOnly == true) {
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
