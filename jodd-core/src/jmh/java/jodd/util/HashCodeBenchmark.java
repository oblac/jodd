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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Tests two approaches of HashCode class: one with fluent interface but instance creation, and the other
 * without instance, but with misleading api.
 *
 * Run:
 * <code>
 * gw :jodd-core:HashCodeBenchmark
 * </code>
 * <p>
 * Results:
 * <pre>
 * Benchmark                                 Mode  Cnt          Score          Error  Units
 * HashCodeBenchmark.hashCode_noInstance    thrpt   10  418009852.674 ±  8233326.541  ops/s
 * HashCodeBenchmark.hashCode_withInstance  thrpt   10  413934971.659 ± 15330555.186  ops/s
 * </pre>
 */
@Fork(1)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class HashCodeBenchmark {

	public static class HashCode_NoInstance {

		private static final int C1 = 0xcc9e2d51;
		private static final int C2 = 0x1b873593;

		public static final int SEED = 173;
		public static final int PRIME = 37;

		/**
		 * Calculates hash code for ints.
		 */
		public static int hash(final int seed, final int anInt) {
			return (PRIME * seed) + anInt;
		}
	}

	public static class HashCode_withInstance {

		private static final int C1 = 0xcc9e2d51;
		private static final int C2 = 0x1b873593;
		private final int prime;
		private int hashcode;

		public static HashCode_withInstance create() {
			return new HashCode_withInstance(173, 37);
		}

		public static HashCode_withInstance create(final int seed, final int prime) {
			return new HashCode_withInstance(seed, prime);
		}

		private HashCode_withInstance(final int seed, final int prime) {
			this.prime = prime;
			this.hashcode = seed;
		}

		public int get() {
			return hashcode;
		}

		public HashCode_withInstance hash(final int anInt) {
			hashcode = (prime * hashcode) + anInt;
			return this;
		}
	}

	@Benchmark
	public int hashCode_noInstance() {
		int hash = HashCode_NoInstance.SEED;
		for (int i = 0; i < 10; i++) {
			hash = HashCode_NoInstance.hash(hash, i);
		}
		return hash;
	}

	@Benchmark
	public int hashCode_withInstance() {
		HashCode_withInstance hc = HashCode_withInstance.create();
		for (int i = 0; i < 10; i++) {
			hc.hash(i);
		}
		return hc.get();
	}
}