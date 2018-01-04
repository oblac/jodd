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

import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;

/**
 * benchmark for comparing <tt>java.util.Random</tt> and <tt>java.util.concurrent.ThreadLocalRandom</tt> usages in {@link MathUtil}. <br/>
 * Old code (with Random-usage) is copied directly into appropriate benchmark-methods.
 *
 * results:
 * <pre>
 * Benchmark                                                   Mode  Cnt          Score        Error  Units
 * MathUtilRandomBenchmark.randomInt_with_Random              thrpt   50   42254442,629 ± 179546,676  ops/s
 * MathUtilRandomBenchmark.randomInt_with_ThreadLocalRandom   thrpt   50   98595791,642 ± 331995,807  ops/s
 * MathUtilRandomBenchmark.randomLong_with_Random             thrpt   50   42681025,726 ± 190306,059  ops/s
 * MathUtilRandomBenchmark.randomLong_with_ThreadLocalRandom  thrpt   50  101363963,141 ± 361527,716  ops/s
 * </pre>
 */
@Fork(5)
@Warmup(iterations = 2)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class MathUtilRandomBenchmark {

	private long long_inclusive;
	private long long_exclusive;
	private int int_inclusive;
	private int int_exclusive;

	@Setup
	public void prepare() {
		long_inclusive = RandomUtils.nextLong(1L, 345678953L);
		long_exclusive = long_inclusive + RandomUtils.nextLong(1L, 100000L);
		int_inclusive = RandomUtils.nextInt(1, 345678953);
		int_exclusive = int_inclusive + RandomUtils.nextInt(1, 100000);
	}

	// ----------------------------------------------------------------------- Jodd MathUtil Code with Random
	//		old code with Random is copied into these benchmark methods

	@Benchmark
	public long randomLong_with_Random() {
		return long_inclusive + (long)(Math.random() * (long_exclusive - long_inclusive));
	}

	@Benchmark
	public long randomInt_with_Random() {
		return int_inclusive + (int)(Math.random() * (int_exclusive - int_inclusive));
	}

	// ----------------------------------------------------------------------- Jodd MathUtil with ThreadLocalRandom

	@Benchmark
	public long randomLong_with_ThreadLocalRandom() {
		return MathUtil.randomLong(long_inclusive, long_exclusive);
	}

	@Benchmark
	public long randomInt_with_ThreadLocalRandom() {
		return MathUtil.randomInt(int_inclusive, int_exclusive);
	}

}
