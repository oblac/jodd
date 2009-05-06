package jodd.gfx.delay;

/**
 * Returns one delayer according to system environment and
 * application needs.
 */
public class DelayerFactory {

	public Delayer getBestAvailiable() {
		Delayer result = null;

		// Try SDK 1.4.2
		try {
			Class.forName("sun.misc.Perf");
			result = new PerfDelayer();
		} catch (Exception ex) {
			// ignore
		}

		// default delayer
		if (result == null) {
			result = new DefaultDelayer();
		}
		return result;
	}
}
