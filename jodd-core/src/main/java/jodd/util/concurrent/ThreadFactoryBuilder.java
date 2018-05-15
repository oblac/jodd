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

package jodd.util.concurrent;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Fluent {@code ThreadFactory} builder.
 */
public class ThreadFactoryBuilder {

	private String nameFormat;
	private Boolean daemonThread;
	private Integer priority;
	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;
	private ThreadFactory backingThreadFactory = null;

	/**
	 * Returns new {@code ThreadFactory} builder.
	 */
	public static ThreadFactoryBuilder create() {
		return new ThreadFactoryBuilder();
	}

	/**
	 * Sets the printf-compatible naming format for threads.
	 * Use {@code %d} to replace it with the thread number.
	 */
	public ThreadFactoryBuilder setNameFormat(final String nameFormat) {
		this.nameFormat = nameFormat;
		return this;
	}

	/**
	 * Sets if new threads will be daemon.
	 */
	public ThreadFactoryBuilder setDaemon(final boolean daemon) {
		this.daemonThread = daemon;
		return this;
	}

	/**
	 * Sets the threads priority.
	 */
	public ThreadFactoryBuilder setPriority(final int priority) {
		this.priority = priority;
		return this;
	}

	/**
	 * Sets the {@code UncaughtExceptionHandler} for new threads created.
	 */
	public ThreadFactoryBuilder setUncaughtExceptionHandler(
		final Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {

		this.uncaughtExceptionHandler = Objects.requireNonNull(uncaughtExceptionHandler);
		return this;
	}

	/**
	 * Sets the backing {@code ThreadFactory} for new threads. Threads
	 * will be created by invoking {@code newThread(Runnable} on this backing factory.
	 */
	public ThreadFactoryBuilder setBackingThreadFactory(final ThreadFactory backingThreadFactory) {
		this.backingThreadFactory = Objects.requireNonNull(backingThreadFactory);
		return this;
	}

	/**
	 * Returns a new thread factory using the options supplied during the building process. After
	 * building, it is still possible to change the options used to build the ThreadFactory and/or
	 * build again.
	 */
	public ThreadFactory get() {
		return get(this);
	}

	private static ThreadFactory get(final ThreadFactoryBuilder builder) {
		final String nameFormat = builder.nameFormat;
		final Boolean daemon = builder.daemonThread;
		final Integer priority = builder.priority;
		final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = builder.uncaughtExceptionHandler;

		final ThreadFactory backingThreadFactory =
			(builder.backingThreadFactory != null)
				? builder.backingThreadFactory
				: Executors.defaultThreadFactory();

		final AtomicLong count = (nameFormat != null) ? new AtomicLong(0) : null;

		return runnable -> {
			final Thread thread = backingThreadFactory.newThread(runnable);
			if (nameFormat != null) {
				final String name = String.format(nameFormat, count.getAndIncrement());

				thread.setName(name);
			}
			if (daemon != null) {
				thread.setDaemon(daemon);
			}
			if (priority != null) {
				thread.setPriority(priority);
			}
			if (uncaughtExceptionHandler != null) {
				thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
			}
			return thread;
		};
	}

}