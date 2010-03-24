// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.gfx.delay;

/**
 * Returns one delayer according to system environment and
 * application needs. available
 */
public class DelayerFactory {


	public static Delayer getBestAvailable() {
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
