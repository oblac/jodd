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

	public TimedCache(final long timeout) {
		this.cacheSize = 0;
		this.timeout = timeout;
		cacheMap = new HashMap<>();
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
			if (co.isExpired()) {
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
	public void schedulePrune(final long delay) {
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
