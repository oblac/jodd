// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timed cache. Not limited by size, objects are removed only when they are expired.
 * Prune is not invoked explicitly by standard {@link Cache} methods, however,
 * it is possible to schedule prunes on fined-rate delays.
 */
public class TimedCache<K, V> extends AbstractCacheMap<K, V> {

	public TimedCache(long timeout) {
		this.cacheSize = 0;
		this.timeout = timeout;
		cacheMap = new HashMap<K, CacheObject<K,V>>();
	}

	// ---------------------------------------------------------------- prune

	/**
	 * Prunes expired elements from the cache. Returns the number of removed objects.
	 */
	@Override
	protected int pruneCache() {
        int count = 0;
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject co = values.next();
			if (co.isExpired() == true) {
				values.remove();
				count++;
			}
		}
		return count;
	}


	// ---------------------------------------------------------------- auto prune

	protected Timer pruneTimer;

	/**
	 * Schedules prune.
	 */
	public void schedulePrune(long delay) {
		if (pruneTimer != null) {
			pruneTimer.cancel();
		}
		pruneTimer = new Timer();
		pruneTimer.schedule(
				new TimerTask() {
					@Override
					public void run() {
						prune();
					}
				}, delay, delay
		);
	}

	/**
	 * Cancels prune schedules.
	 */
	public void cancelPruneSchedule() {
		if (pruneTimer != null) {
			pruneTimer.cancel();
			pruneTimer = null;
		}
	}

}
