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

package jodd.util.function;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Slightly better Maybe monad. It does not have a {@code get()}, but it has {@code or()} :).
 * Slightly better implementation of Optional, as there are no IF blocks all over the class.
 */
public interface Maybe<T> extends Iterable<T> {

	/**
	 * Static factory for Maybe.
	 */
	static <T> Maybe<T> of(final T value) {
		if (value == null) {
			return nothing();
		}
		return just(value);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	static <T> Maybe<T> of(final Optional<T> optionalValue) {
		return optionalValue.map(Maybe::of).orElseGet(Maybe::nothing);
	}

	/**
	 * Returns {@code true} if value is present.
	 */
	boolean isJust();

	/**
	 * Returns {@code false} if value is not present.
	 */
	boolean isNothing();

	/**
	 * Takes a default value and a function. If the Maybe value is Nothing, the function returns the default value.
	 * Otherwise, it applies the function to the value inside the Just and returns the result.
	 */
	<V> V maybe(V defaultValue, Function<T, V> function);

	/**
	 * Consumes value if present.
	 */
	void consumeJust(Consumer<T> consumer);

	/**
	 * Returns empty or single-element stream.
	 */
	Stream<T> stream();

	/**
	 * Use a maybe of given value if this one is NOTHING.
	 */
	Maybe<T> or(T otherValue);

	/**
	 * Use give maybe if this one is NOTHING.
	 */
	Maybe<T> or(Maybe<T> maybe);

	/**
	 * Use supplied value if this one is NOTHING.
	 */
	Maybe<T> or​(Supplier<? extends Maybe<? extends T>> supplier);

	/**
	 * Map.
	 */
	<U> Maybe<U> map(Function<? super T, ? extends U> mapper);

	/**
	 * Flat map.
	 */
	<U> Maybe<U> flatMap(Function<? super T, Maybe<U>> mapper);

	Maybe<T> filter(Predicate<? super T> mapping);

	/**
	 * Converts Maybe to the Java Optional.
	 */
	Optional<T> optional();

	/**
	 * Returns a NOTHING.
	 */
	static <T> Maybe<T> nothing() {
		return new Maybe<T>() {
			@Override
			public boolean isJust() {
				return false;
			}

			@Override
			public boolean isNothing() {
				return true;
			}

			@Override
			public <V> V maybe(final V defaultValue, final Function<T, V> function) {
				return defaultValue;
			}

			@Override
			public void consumeJust(final Consumer<T> consumer) {
			}

			@Override
			public Iterator<T> iterator() {
				return Collections.<T>emptyList().iterator();
			}

			@Override
			public Stream<T> stream() {
				return Stream.empty();
			}

			@Override
			public Maybe<T> or(final T otherValue) {
				return Maybe.of(otherValue);
			}

			@Override
			public Maybe<T> or(final Maybe<T> maybeJust) {
				return maybeJust;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Maybe<T> or​(final Supplier<? extends Maybe<? extends T>> supplier) {
				final Maybe<T> r = (Maybe<T>) supplier.get();
				return Objects.requireNonNull(r);
			}

			@Override
			public <U> Maybe<U> map(final Function<? super T, ? extends U> mapper) {
				return nothing();
			}

			@Override
			public <U> Maybe<U> flatMap(final Function<? super T, Maybe<U>> mapper) {
				return nothing();
			}

			@Override
			public Maybe<T> filter(final Predicate<? super T> mapping) {
				return nothing();
			}

			@Override
			public String toString() {
				return "nothing";
			}

			@Override
			public boolean equals(final Object obj) {
				return false;
			}

			@Override
			public int hashCode() {
				return 0;
			}

			@Override
			public Optional<T> optional() {
				return Optional.empty();
			}
		};
	}

	/**
	 * Returns a JUST>
	 */
	static <T> Maybe<T> just(final T theValue) {
		return new Just<>(theValue);
	}

	class Just<T> implements Maybe<T> {
		private final T value;

		private Just(final T value) {
			Objects.requireNonNull(value);
			this.value = value;
		}

		@Override
		public boolean isJust() {
			return true;
		}

		@Override
		public boolean isNothing() {
			return false;
		}

		@Override
		public <V> V maybe(final V defaultValue, final Function<T, V> function) {
			return function.apply(value);
		}

		@Override
		public void consumeJust(final Consumer<T> consumer) {
			consumer.accept(value);
		}

		@Override
		public Iterator<T> iterator() {
			return Collections.singleton(value).iterator();
		}

		@Override
		public Stream<T> stream() {
			return Stream.of(value);
		}

		@Override
		public Maybe<T> or(final T otherValue) {
			return this;
		}

		@Override
		public Maybe<T> or(final Maybe<T> maybeDefaultValue) {
			return this;
		}

		@Override
		public Maybe<T> or​(final Supplier<? extends Maybe<? extends T>> supplier) {
			return this;
		}

		@Override
		public <U> Maybe<U> map(final Function<? super T, ? extends U> mapper) {
			return Maybe.of(mapper.apply(value));
		}

		@Override
		public <U> Maybe<U> flatMap(final Function<? super T, Maybe<U>> mapper) {
			return mapper.apply(value);
		}

		@Override
		public Maybe<T> filter(final Predicate<? super T> predicate) {
			return predicate.test(value) ? this : nothing();
		}

		@Override
		public String toString() {
			return "just: " + value.toString();
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			final Just<?> that = (Just<?>) o;

			return value.equals(that.value);

		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public Optional<T> optional() {
			return Optional.of(value);
		}
	}
}

