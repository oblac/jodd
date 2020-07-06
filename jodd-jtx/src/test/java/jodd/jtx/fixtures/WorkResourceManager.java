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

import jodd.jtx.JtxResourceManager;
import jodd.jtx.JtxTransactionMode;

import java.util.concurrent.atomic.AtomicInteger;

public class WorkResourceManager implements JtxResourceManager<WorkSession> {

	public AtomicInteger txno = new AtomicInteger();

	@Override
	public Class<WorkSession> getResourceType() {
		return WorkSession.class;
	}

	@Override
	public WorkSession beginTransaction(final JtxTransactionMode jtxMode, final boolean active) {
		if (!active) {
			return new WorkSession();
		}
		final WorkSession work = new WorkSession(txno.incrementAndGet());
		work.readOnly = jtxMode.isReadOnly();
		return work;
	}

	@Override
	public void commitTransaction(final WorkSession resource) {
		resource.done();
		txno.decrementAndGet();
	}

	@Override
	public void rollbackTransaction(final WorkSession resource) {
		resource.back();
		txno.decrementAndGet();
	}

	@Override
	public void close() {
	}

}
