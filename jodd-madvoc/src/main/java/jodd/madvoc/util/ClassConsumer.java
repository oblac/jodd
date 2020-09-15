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

package jodd.madvoc.util;

import java.util.function.Consumer;

/**
 * Holder for a class and optional consumer of it's instance.
 * Useful when class is instantiated later.
 */
public class ClassConsumer<T> implements Consumer<T> {
	private final Class<T> type;
	private final Consumer<T> consumer;

	public static <R> ClassConsumer<R> of(final Class<R> type) {
		return new ClassConsumer<>(type, null);
	}

	public static <R> ClassConsumer<R> of(final Class<R> type, final Consumer<R> consumer) {
		return new ClassConsumer<>(type, consumer);
	}

	public ClassConsumer(final Class<T> type, final Consumer<T> consumer) {
		this.type = type;
		this.consumer = consumer;
	}

	public Class<T> type() {
		return type;
	}

	public Consumer<T> consumer() {
		return consumer;
	}

	@Override
	public void accept(final T instance) {
		if (consumer != null) {
			consumer.accept(instance);
		}
	}
}
