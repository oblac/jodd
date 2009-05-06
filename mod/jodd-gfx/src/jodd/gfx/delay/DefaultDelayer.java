package jodd.gfx.delay;

/**
 * Default delayer, using <code>Thread.sleep()</code>
 * and <code>System.currentTimeMillis()</code>. This is the most
 * unprecise delayer, especally on Windows platforms.
 */
public class DefaultDelayer extends Delayer {

	@Override
	public void start() {
		start = System.currentTimeMillis();
	}

	@Override
	public boolean end() {
		diff = (System.currentTimeMillis() - start);
		diff *= 1000000L;
		return endAndWait();
	}

	@Override
	public void sleep(long nanoseconds) {
		int delay = (int) (nanoseconds / 1000000L);
		if (delay == 0) {
			delay = 1;
		}
		try {
			Thread.sleep(delay);
		} catch (InterruptedException iex) {
			// ignore
		}
	}
}
