// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.data;

import jodd.jtx.JtxResourceManager;
import jodd.jtx.JtxTransactionMode;

public class WorkResourceManager implements JtxResourceManager<WorkSession> {

	int txno = 1;

	public Class<WorkSession> getResourceType() {
		return WorkSession.class;
	}

	public WorkSession beginTransaction(JtxTransactionMode jtxMode, boolean active) {
		if (active == false) {
			return new WorkSession();
		}
		WorkSession work = new WorkSession(txno++);
		work.readOnly = jtxMode.isReadOnly();
		return work;
	}

	public void commitTransaction(WorkSession resource) {
		resource.done();
		txno--;
	}

	public void rollbackTransaction(WorkSession resource) {
		resource.back();
		txno--;
	}

	public void close() {
	}

}
