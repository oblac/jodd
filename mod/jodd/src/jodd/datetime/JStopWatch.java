// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import java.util.List;
import java.util.ArrayList;

/**
 * Nice thread-aware stopwatch that supports time spans, cumulative times and laps.
 * Useful for all kind of profiling, time measurements etc.
 */
public class JStopWatch {

	private static final ThreadLocal<JStopWatch> THREAD_LOCAL = new ThreadLocal<JStopWatch>();

	/**
	 * Optional stopwatch name.
	 */
	protected String name;
	/**
	 * Last start time.
	 */
	protected long startTime;
	/**
	 * Last stop time.
	 */
	protected long stopTime;
	/**
	 * Last elapsed time.
	 */
	protected long spanTime;
	/**
	 * Cumulative elapsed time.
	 */
	protected long totalTime;
	/**
	 * Running flag.
	 */
	protected boolean running;

	// ---------------------------------------------------------------- thread

	/**
	 * Puts current stopwatch in current thread.
	 */
	protected void assignToCurrentThread() {
		THREAD_LOCAL.set(this);
	}

	/**
	 * Returns thread-assigned stop watch.
	 */
	public static JStopWatch getThreadStopWatch() {
		return THREAD_LOCAL.get();
	}

	// ---------------------------------------------------------------- ctors

	/**
	 * Starts the stopwatch.
	 */
	public JStopWatch() {
		this(false);
	}

	public JStopWatch(boolean putInThread) {
		this("#jStopWatch", putInThread);
	}

	/**
	 * Starts the named stopwatch.
	 */
	public JStopWatch(String name) {
		this(name, false);
	}

	/**
	 * Starts the stopwatch.
	 */
	public JStopWatch(String name, boolean assignToCurrentThread) {
		this.name = name;
		if (assignToCurrentThread) {
			assignToCurrentThread();
		}
		start();
	}

	/**
	 * Returns stopwatch name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns <code>true</code> if stopwatch is running.
	 */
	public boolean isRunning() {
		return running;
	}

	// ---------------------------------------------------------------- basic

	/**
	 * Starts the stopwatch. {@link #stop()} must be called prior to restart.
	 * Returns starting time in milliseconds.
	 */
	public long start() {
		if (running == false) {
			startTime = System.currentTimeMillis();
			running = true;
		}
		return startTime;
	}

	/**
	 * Restarts the stopwatch.
	 */
	public long restart() {
		startTime = System.currentTimeMillis();
		running = true;
		return startTime;
	}

	/**
	 * Stops the stopwatch if running. Returns span time.
	 * If laps are used, marks the last lap.
	 */
	public long stop() {
		if (running == true) {
			stopTime = System.currentTimeMillis();
			if (laps != null) {
				lap(stopTime);
			}
			spanTime = stopTime - startTime;
			totalTime += stopTime - startTime;
			running = false;
		}
		return spanTime;
	}
	
	/**
	 * Returns total elapsed time from the {@link #start()} in ms.
	 */
	public long elapsed() {
		return System.currentTimeMillis() - startTime;
	}

	/**
	 * Stops the stopwatch and returns total cumulative time in ms.
	 */
	public long total() {
		stop();
        return totalTime;
	}

	/**
	 * Stops the stopwatch and returns total time span for last
	 * start-stop sequence.
	 */
	public long span() {
		stop();
		return spanTime;
	}


	// ---------------------------------------------------------------- laps

	/**
	 * List of all laps. Contains long arrays in following format:
	 * <ul>
	 * <li>lap time - current lap time,</li>
	 * <li>lap span time - elapsed time from start,</li>
	 * <li>lap millis - lap milliseconds. 
	 * </ul>
	 */
	protected List<long[]> laps;

	/**
	 * Marks a lap and returns its length. May be called only while stop watch is running.
	 */
	public long lap() {
		return lap(System.currentTimeMillis());
	}

	protected long lap(long lap) {
		if (running == false) {
			return 0;
		}
		long lapSpanTime = lap - startTime;
		long lapTime;
		if (laps == null) {
			lapTime = lapSpanTime;
			laps = new ArrayList<long[]>();
		} else {
			long[] previous = laps.get(laps.size() - 1);
			lapTime = lap - previous[2];
		}
		laps.add(new long[] {lapTime, lapSpanTime, lap});
		return lapTime;
	}

	/**
	 * Returns the total number of laps up to this moment.
	 */
	public int totalLaps() {
		if (laps == null) {
			return 0;
		}
		return laps.size();
	}

	/**
	 * Returns lap times for 1-based lap index.
	 * Returns <code>null</code> if laps are not used or if index is invalid.
	 */
	public long[] getLapTimes(int index) {
		if (laps == null) {
			return null;
		}
		if ((index <= 0) || (index > laps.size())) {
			return null;
		}
		return laps.get(index - 1);
	}

	// ---------------------------------------------------------------- output

	/**
	 * Returns total elapsed time as formatted string from the last start.
	 */
	@Override
	public String toString() {
		long elapsed = elapsed();
		StringBuilder sb = new StringBuilder();
		sb.append("JStopWatch ").append(name).append(running ? " is running." : "").append('\n');
		if (running == true) {
			sb.append("elapsed: ").append(formatTimeSpan(elapsed));
		} else {
			if (spanTime != totalTime) {
				sb.append("span:  ").append(formatTimeSpan(spanTime)).append('\n');
			}
			sb.append("total: ").append(formatTimeSpan(totalTime));
		}
		if (laps != null) {
			if (laps.isEmpty() == false) {
				sb.append('\n');
			}
			for (int i = 0; i < laps.size(); i++) {
				long[] longs = laps.get(i);
				sb.append("  lap #").append(i + 1).append(':').append('\t');
				sb.append(formatTimeSpan(longs[0])).append('\t');
				sb.append(formatTimeSpan(longs[1])).append('\n');
			}
		}
		return sb.toString();
	}

	/**
	 * Formats time spans.
	 */
	public static String formatTimeSpan(long millis) {
		long seconds = 0;
		long minutes = 0;
		long hours = 0;

		if (millis > 1000) {
			seconds = millis / 1000;
			millis %= 1000;
		}
		if (seconds > 60) {
			minutes = seconds / 60;
			seconds %= 60;
		}
		if (minutes > 60) {
			hours = minutes / 60;
			minutes %= 60;
		}

		StringBuilder result = new StringBuilder(20);
		boolean out = false;
		if (hours > 0) {
			result.append(hours).append(':');
			out = true;
		}
		if ((out == true) || (minutes > 0)) {
			if (minutes < 10) {
				result.append('0');
			}
			result.append(minutes).append(':');
		}

		if (seconds < 10) {
			result.append('0');
		}
		result.append(seconds).append('.');

		if (millis < 10) {
			result.append('0');
		}
		if (millis < 100) {
			result.append('0');
		}
		result.append(millis);
		return result.toString();
	}
}
