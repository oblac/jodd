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
 * Run:
 * <code>
 * gw :jodd-core:perf -PStringBandBenchmark
 * </code>
 * <p>
 * Results:
 * <pre>
 * string2: 300
 * string3: 148
 * ------------
 * stringBand2: 262 (it slower then Java, for strings <= 16 chars)
 * stringBand3: 191 (its faster for any longer strings)
 * </pre>
 */
@Fork(1)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class StringBandBenchmark {

	private String[] strings = new String[5];

	@Setup
	public void prepare() {
		for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
			strings[i] = new RandomString().randomAlphaNumeric(8);
		}
	}

	@Benchmark
	public String string2() {
		return strings[1] + strings[2];
	}

	@Benchmark
	public String stringBand2() {
		return new StringBand(2)
			.append(strings[1])
			.append(strings[2])
			.toString();
	}

	@Benchmark
	public String string3() {
		return strings[1] + strings[2] + strings[3];
	}

	@Benchmark
	public String stringBand3() {
		return new StringBand(3)
			.append(strings[1])
			.append(strings[2])
			.append(strings[3])
			.toString();
	}
}
