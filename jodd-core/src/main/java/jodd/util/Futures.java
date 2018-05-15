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

package jodd.util;

import jodd.util.concurrent.ThreadFactoryBuilder;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Some Java 8 futures utilities.
 */
public class Futures {

	private static final ScheduledExecutorService SCHEDULER =
		Executors.newScheduledThreadPool(
			1,
			ThreadFactoryBuilder
				.create()
				.setDaemon(true)
				.setNameFormat("failAfter-%d")
				.get());

	/**
	 * Returns {@code CompletableFuture} that fails after certain number of milliseconds.
	 */
	public static <T> CompletableFuture<T> failAfter(final long duration) {
		final CompletableFuture<T> promise = new CompletableFuture<>();
		SCHEDULER.schedule(() -> {
			final TimeoutException ex = new TimeoutException("Timeout after " + duration);
			return promise.completeExceptionally(ex);
		}, duration, MILLISECONDS);
		return promise;
	}

	/**
	 * Returns {@code CompletableFuture} that fails after certain amount of time.
	 */
	public static <T> CompletableFuture<T> failAfter(final Duration duration) {
		return failAfter(duration.toMillis());
	}

	public static <T> CompletableFuture<T> within(final CompletableFuture<T> future, final Duration duration) {
		final CompletableFuture<T> timeout = failAfter(duration);
		return future.applyToEither(timeout, Function.identity());
	}

	public static <T> CompletableFuture<T> within(final CompletableFuture<T> future, final long duration) {
		final CompletableFuture<T> timeout = failAfter(duration);
		return future.applyToEither(timeout, Function.identity());
	}

}
