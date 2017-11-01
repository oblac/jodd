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
 * Benchmark                                                                      Mode  Cnt          Score          Error  Units
 * Base64Benchmark.decode_Apache_Base64                                          thrpt   10     423695.794 ±    28533.549  ops/s
 * Base64Benchmark.decode_Java_Base64                                            thrpt   10    1265582.300 ±    33158.561  ops/s
 * Base64Benchmark.decode_Jodd_Base64                                            thrpt   10    2153548.899 ±    61355.151  ops/s
 * Base64Benchmark.encode_Apache_Base64                                          thrpt   10     437170.067 ±     9896.702  ops/s
 * Base64Benchmark.encode_Java_Base64                                            thrpt   10    2345220.500 ±    76198.905  ops/s
 * Base64Benchmark.encode_Jodd_Base64                                            thrpt   10    2369976.451 ±   111352.672  ops/s
 * CharUtilIWhitespaceBenchmark.isWhitespace_Java                                thrpt   10   14331949.693 ±   463362.791  ops/s
 * CharUtilIWhitespaceBenchmark.isWhitespace_Jodd                                thrpt   10  346290107.476 ± 13771853.466  ops/s
 * StringBandBenchmark.string2                                                   thrpt   10   36780068.335 ±  1184388.514  ops/s
 * StringBandBenchmark.string3                                                   thrpt   10   16942855.967 ±   853722.889  ops/s
 * StringBandBenchmark.stringBand2                                               thrpt   10   27967094.903 ±   724524.916  ops/s
 * StringBandBenchmark.stringBand3                                               thrpt   10   20909453.929 ±   788585.662  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringNoMatch          thrpt   21   30955737.221 ±   269888.167  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringOneMatch         thrpt   21    8267635.892 ±   268938.864  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringSeveralMatches   thrpt   21    4769646.746 ±   141350.031  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringNoMatch         thrpt   21  203482907.693 ±  6869116.349  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringOneMatch        thrpt   21   13189304.405 ±   225885.341  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringSeveralMatches  thrpt   21    7154996.719 ±   267083.568  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringNoMatch                     thrpt   21    6134190.139 ±   211500.974  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringOneMatch                    thrpt   21    1835404.310 ±    48717.545  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringSeveralMatches              thrpt   21    1378140.957 ±    26445.602  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringNoMatch                    thrpt   21    7332052.151 ±   279770.916  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringOneMatch                   thrpt   21    4229701.851 ±   330467.125  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringSeveralMatches             thrpt   21    2886766.328 ±    48066.661  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringNoMatch                 thrpt   21   31050291.182 ±   309338.753  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringOneMatch                thrpt   21   10034031.465 ±   157083.396  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringSeveralMatches          thrpt   21    4991154.668 ±    93731.674  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringNoMatch                thrpt   21  207654544.276 ±  2141371.945  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringOneMatch               thrpt   21   18967291.468 ±   728185.876  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringSeveralMatches         thrpt   21    9217583.423 ±   266449.122  ops/s
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
