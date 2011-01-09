// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.idgen;

/**
 * Simple synchronized int ids sequence generator. It takes the positive sequence range (boundaries are included).
 * Optionally, it supports cycling, when counter reaches the max value. Otherwise an exception is thrown.
 * @see SimpleLongIdGenerator
 */
public class SimpleIdGenerator {

	protected volatile int value;

	protected int initialValue;
	protected int maxValue;
	protected boolean cycle;

	/**
	 * Creates a new default cycled id generator. Starts from 1 and counts up to max int value.
	 */
	public SimpleIdGenerator() {
		this(1, Integer.MAX_VALUE, true);
	}

	/**
	 * Creates a new cycled id generator with specified initial value.
	 */
	public SimpleIdGenerator(int initialValue) {
		this(initialValue, Integer.MAX_VALUE, true);
	}

	/**
	 * Creates a new cycled id generator with specified range.
	 */
	public SimpleIdGenerator(int initialValue, int maxValue) {
		this(initialValue, maxValue, true);
	}

	/**
	 * Creates a new id generator with specified range and cycling flag.
	 */
	public SimpleIdGenerator(int initialValue, int maxValue, boolean cycle) {
		if (initialValue < 0) {
			throw new IllegalArgumentException("Initial value '" + initialValue + "' must be a positive number.");
		}
		if (maxValue <= initialValue) {
			throw new IllegalArgumentException("Max value '" + maxValue + "' is less or equals to initial value '" + initialValue + "'.");
		}
		this.initialValue = this.value = initialValue;
		this.maxValue = maxValue;
		this.cycle = cycle;
	}

	/**
	 * Returns the next value from the sequence. Thread-safe.
	 */
	public synchronized int next() {
		int id = value;

		value++;
		if ((value > maxValue) || (value < 0)) {
			if (cycle == false) {
				throw new IllegalStateException("Max value already reached.");
			}
			value = initialValue;
		}
		return id;
	}
}
