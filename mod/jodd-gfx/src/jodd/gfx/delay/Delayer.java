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

	protected long start;

	/**
	 * Indicates the beggining of the loop. Must store
	 * any kind of time data in <code>start</code> attribute.
	 */
	public abstract void start();

	protected long diff;

	/**
	 * Indicates the end of the loop. Performs all necesarry waitings.
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
	 * Internal logic waiting logic.
	 */
	protected final boolean endAndWait() {
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
	 * Sleep in nanoseconds
	 * @param nanoseconds	time to sleep
	 */
	public abstract void sleep(long nanoseconds);
}
