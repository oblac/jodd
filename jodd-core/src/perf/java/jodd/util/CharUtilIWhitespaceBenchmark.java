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

import org.openjdk.jmh.annotations.*;

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
 * gw :jodd-core:perf -PCharUtilIsWhitespaceBenchmark
 * </code>
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
	public void isWhitespace_Java() {
		for (int c = 0 ; c < chars.length ; c++) {
			Character.isWhitespace(chars[c]);
		}
	}

	@Benchmark
	public void isWhitespace_Jodd() {
		for (int c = 0 ; c < chars.length ; c++) {
			CharUtil.isWhitespace(chars[c]);
		}
	}
}
