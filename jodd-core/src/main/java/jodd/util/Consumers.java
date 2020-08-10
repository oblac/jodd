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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * An aggregate consumer.
 */
public class Consumers<T> implements Consumer<T> {

	private final List<Consumer<T>> consumerList = new ArrayList<>();
	private boolean parallel = false;

	public Consumers(final Consumer<T>... consumers) {
		Collections.addAll(consumerList, consumers);
	}
	public Consumers(final Iterable<Consumers<T>> consumers) {
		consumers.forEach(consumerList::add);
	}

	@SuppressWarnings("unchecked")
	public static <R> Consumers<R> empty() {
		return new Consumers<>();
	}

	/**
	 * Creates an aggregate from given consumers.
	 */
	public static <R> Consumers<R> of(final Consumer<R>... consumers) {
		return new Consumers<>(consumers);
	}

	@SuppressWarnings("unchecked")
	public static <R> Consumers<R> of(final Iterable<Consumer<R>> consumers) {
		return new Consumers(consumers);
	}

	/**
	 * Registers an additional consumer.
	 */
	public Consumers<T> add(final Consumer<T> consumer) {
		consumerList.add(consumer);
		return this;
	}

	/**
	 * Registers additional consumers.
	 */
	public Consumers<T> addAll(final Consumer<T>... consumers) {
		Collections.addAll(consumerList, consumers);
		return this;
	}

	/**
	 * Defines if consumer acceptance should be parallel.
	 */
	public Consumers<T> parallel(final boolean parallel) {
		this.parallel = parallel;
		return this;
	}

	/**
	 * Consumes all registered consumers. The are executed sequentially, in order of registration.
	 * If {@link #parallel(boolean)} flag is set, consumption is going to be parallel.
	 */
	@Override
	public void accept(final T t) {
		if (parallel) {
			consumerList.parallelStream().forEach(consumer -> consumer.accept(t));
		}
		else {
			consumerList.forEach(consumer -> consumer.accept(t));
		}
	}

	public boolean isEmpty() {
		return consumerList.isEmpty();
	}

	/**
	 * Clears the list of consumers.
	 */
	public Consumer clear() {
		consumerList.clear();
		return this;
	}

	/**
	 * Removes a consumer.
	 */
	public Consumers<T> remove(final Consumer<T> consumer) {
		consumerList.remove(consumer);
		return this;
	}
}
