// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns {@link DbSession} assigned to current thread.
 * @see DbThreadSession
 * @see ThreadDbSessionHolder
 */
public class ThreadDbSessionProvider implements DbSessionProvider {

	private static final Logger log = LoggerFactory.getLogger(ThreadDbSessionProvider.class);

	/**
	 * {@inheritDoc}
	 */
	public DbSession getDbSession() {
		if (log.isDebugEnabled()) {
			log.debug("Requesting thread session");
		}

		DbSession session = ThreadDbSessionHolder.get();

		if (session == null) {
			throw new DbSqlException(
					"No DbSession associated with current thread." +
					"It seems that ThreadDbSessionHolder is not used.");
		}
		return session;
	}


}
