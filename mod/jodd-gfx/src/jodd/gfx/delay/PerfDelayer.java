// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.gfx.delay;

import sun.misc.Perf;
/**
 * Uses undocumented Sun timings that is available only in SDK v1.4.2 and newer.
 * <p>
 *
 * This updater locks CPU to 100%.
 */
public class PerfDelayer extends Delayer {

	private Perf perf;
	private long invCountFreq;

	public PerfDelayer() {
		perf = Perf.getPerf();
		long countFreq = perf.highResFrequency();
		invCountFreq = 1000000000L / countFreq;
	}

	@Override
	public void start() {
		start = perf.highResCounter();
	}

	@Override
	public boolean end() {
		diff = (perf.highResCounter() - start) * invCountFreq; //1000000000L / countFreq;
		return endAndWait();
	}

	@Override
	public void sleep(long nanoSeconds) {
		long count1 = perf.highResCounter();
		long diff = 0;
		while (diff < nanoSeconds) {
			diff = (perf.highResCounter() - count1) * invCountFreq; //1000000000L / countFreq ;
			Thread.yield();
		}
	}
}
