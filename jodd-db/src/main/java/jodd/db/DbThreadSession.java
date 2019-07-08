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

package jodd.db;

import jodd.db.connection.ConnectionProvider;

/**
 * Thread assigned {@link jodd.db.DbSession}. Upon creation, it assigns
 * the session to current thread. Useful when only one session (i.e. connection)
 * is used per thread, through service layers.
 * <p>
 * {@link jodd.db.DbThreadSession} uses {@link jodd.db.ThreadDbSessionHolder} for storing
 * created sessions in the thread storage. Note that holder may be manipulated from outside
 * of this class.
 */
public class DbThreadSession extends DbSession {

	/**
	 * Creates new db session and assigns it to the current thread.
	 * Closes already assigned session, if any exist. 
	 * @param connectionProvider connection provider
	 */
	public DbThreadSession(final ConnectionProvider connectionProvider) {
		super(connectionProvider);

		final DbSession session = ThreadDbSessionHolder.get();
		if (session != null) {
			session.closeSession();
		}
		ThreadDbSessionHolder.set(this);
	}

	/**
	 * Creates new db session and assigns it to the current thread, using
	 * default connection provider.
	 */
	public DbThreadSession() {
		this(null);
	}

	
	/**
	 * Closes current session and remove the association from current thread.
	 * @see jodd.db.DbSession#closeSession()
	 */
	@Override
	public void closeSession() {
		ThreadDbSessionHolder.remove();
		super.closeSession();
	}

	// ---------------------------------------------------------------- static stuff

	/**
	 * Returns current thread session or <code>null</code> if no session is assigned
	 * to a thread.
	 */
	public static DbSession getCurrentSession() {
		return ThreadDbSessionHolder.get();
	}

	/**
	 * Returns existing thread session, or new one if already not exist. If session doesn't exist, it will be created
	 * using default connection provider.
	 */
	public static DbThreadSession getThreadSession() {
		DbThreadSession session = (DbThreadSession) ThreadDbSessionHolder.get();
		if (session == null) {
			session = new DbThreadSession();
		}
		return session;
	}

	/**
	 * Closes thread session.
	 */
	public static void closeThreadSession() {
		DbThreadSession session = (DbThreadSession) ThreadDbSessionHolder.get();
		if (session != null) {
			session.closeSession();
		}
	}

}
