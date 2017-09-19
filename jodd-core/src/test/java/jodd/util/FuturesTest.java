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

import jodd.mutable.MutableInteger;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FuturesTest {

	@Test
	public void testWithinBreaksAsyncExecution() throws ExecutionException, InterruptedException {
		final CompletableFuture<String> asyncCode = CompletableFuture.supplyAsync(() -> {
			ThreadUtil.sleep(3000);
			return "done";
		});

		String value = Futures.within(asyncCode, Duration.ofSeconds(1))
			.exceptionally(throwable -> null)
			.get();

		if (value != null) {
			fail("Test waited so we failed!");
		}
	}

	@Test
	public void testWithinAsyncFaster() throws ExecutionException, InterruptedException {
		final CompletableFuture<String> asyncCode = CompletableFuture.supplyAsync(() -> {
			ThreadUtil.sleep(1000);
			return "done";
		});

		String value = Futures.within(asyncCode, Duration.ofSeconds(5))
			.exceptionally(throwable -> null)
			.get();

		if (value == null) {
			fail("error");
		}
	}

	@Test
	public void testFailAfterOnManyTasks() throws ExecutionException, InterruptedException {
		MutableInteger execCount = new MutableInteger();
		MutableInteger interruptCount = new MutableInteger();

		final CompletableFuture<String> asyncCode1 = CompletableFuture.supplyAsync(() -> {
			ThreadUtil.sleep(1000);
			execCount.value++;
			return "done";
		});
		final CompletableFuture<String> asyncCode2 = CompletableFuture.supplyAsync(() -> {
			ThreadUtil.sleep(2000);
			execCount.value++;
			return "done";
		});
		final CompletableFuture<String> asyncCode3 = CompletableFuture.supplyAsync(() -> {
			ThreadUtil.sleep(3000);
			execCount.value++;
			return "done";
		});

		CompletableFuture<String> f1 = Futures.within(asyncCode1, Duration.ofMillis(1500)).exceptionally(throwable -> { interruptCount.value++; return null; });
		CompletableFuture<String> f2 = Futures.within(asyncCode2, Duration.ofMillis(1500)).exceptionally(throwable -> { interruptCount.value++; return null; });
		CompletableFuture<String> f3 = Futures.within(asyncCode3, Duration.ofMillis(1500)).exceptionally(throwable -> { interruptCount.value++; return null; });

		CompletableFuture.allOf(f1, f2, f3).get();

		assertEquals(1, execCount.value);
		assertEquals(2, interruptCount.value);
	}

}
