// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.gfx.delay;

public abstract class Delayer {

	private long defaultDelay;

	/**
	 * Sets default loop delay in nanoseconds.
 	 * @param nanos		delay in nanoseconds
	 */
	public final void setDefaultDelay(long nanos) {
		defaultDelay = nanos;
	}

	/**
	 * Indicates the beginning of the loop. Must store
	 * any kind of time data in <code>start</code> attribute.
	 */
	public abstract void start();

	/**
	 * Indicates the end of the loop. Performs all necessary waiting.
	 * Must store period length in nanoseconds in <code>diff</code> attribute,
	 * prior to call to <code>endAntWait</code>
	 *
	 */
	public abstract boolean end();

	/**
	 * Excess time from previous frame.
	 */
	private long excess;

	/**
	 * Main waiting logic.
	 */
	protected final boolean waitFor(long diff) {
		long delta = defaultDelay - diff - excess;
		if (delta > 0) {
			sleep(delta);
		} else {
			excess = -delta;
			if (excess >= defaultDelay) {
				return false;
			}
		}
		return true;
	}

    /**
	 * Sleep in nanoseconds.
	 * @param nanoSeconds	time to sleep
	 */
	public abstract void sleep(long nanoSeconds);
}
