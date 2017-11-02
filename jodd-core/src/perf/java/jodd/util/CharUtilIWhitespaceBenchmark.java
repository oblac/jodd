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
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Benchmark for {@link CharUtil#isWhitespace(char)} method. <br/>
 * Following methods will be compared:
 * <ol>
 *     <li>{@link Character#isWhitespace(char)}</li>
 *     <li>{@link CharUtil#isWhitespace(char)}</li>
 * </ol>
 *
 * Run:
 * <code>
 * gw :jodd-core:CharUtilIWhitespaceBenchmark
 * </code>
 * <p>
 * Results:
 * <pre>
 * Benchmark                                                                      Mode  Cnt          Score          Error  Units
 * CharUtilIWhitespaceBenchmark.isWhitespace_Java                                thrpt   10   13583774.700 ±   213854.012  ops/s
 * CharUtilIWhitespaceBenchmark.isWhitespace_Jodd                                thrpt   10  328635894.937 ± 15281486.519  ops/s
 * </pre>
 */
@Fork(1)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class CharUtilIWhitespaceBenchmark {

	private char[] chars = new char[256];

	@Setup
	public void prepare() {
		for (int c = 0 ; c < chars.length ; c++) {
			chars[c] = (char) c;
		}
	}

	@Benchmark
	public boolean isWhitespace_Java() {
		boolean result = false;
		for (char aChar : chars) {
			result = result || Character.isWhitespace(aChar);
		}
		return result;
	}

	@Benchmark
	public boolean isWhitespace_Jodd() {
		boolean result = false;
		for (char aChar : chars) {
			result = result || CharUtil.isWhitespace(aChar);
		}
		return result;
	}
}
