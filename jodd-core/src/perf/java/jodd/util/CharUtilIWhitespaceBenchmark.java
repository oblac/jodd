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
 * gw :jodd-core:perf -PCharUtilIsWhitespaceBenchmark
 * </code>
 * <p>
 * Results:
 * <pre>
 * Benchmark                                                                      Mode  Cnt          Score          Error  Units
 * Base64Benchmark.decode_Apache_Base64                                          thrpt   10     374448.183 ±     9019.145  ops/s
 * Base64Benchmark.decode_Java_Base64                                            thrpt   10    1158992.087 ±    58429.770  ops/s
 * Base64Benchmark.decode_Jodd_Base64                                            thrpt   10    1995540.839 ±    69155.682  ops/s
 * Base64Benchmark.encode_Apache_Base64                                          thrpt   10     390833.312 ±    32782.847  ops/s
 * Base64Benchmark.encode_Java_Base64                                            thrpt   10    2107696.944 ±    70326.325  ops/s
 * Base64Benchmark.encode_Jodd_Base64                                            thrpt   10    2109913.095 ±    63445.711  ops/s
 * CharUtilIWhitespaceBenchmark.isWhitespace_Java                                thrpt   10   13583774.700 ±   213854.012  ops/s
 * CharUtilIWhitespaceBenchmark.isWhitespace_Jodd                                thrpt   10  328635894.937 ± 15281486.519  ops/s
 * StringBandBenchmark.string2                                                   thrpt   10   31029353.194 ±  1811678.405  ops/s
 * StringBandBenchmark.string3                                                   thrpt   10   14626248.717 ±   790672.725  ops/s
 * StringBandBenchmark.stringBand2                                               thrpt   10   17526869.533 ±  5751065.262  ops/s
 * StringBandBenchmark.stringBand3                                               thrpt   10   15486452.961 ±  1382006.825  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringNoMatch          thrpt   21   27395231.094 ±  2330643.753  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringOneMatch         thrpt   21    7587161.270 ±   615480.263  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringSeveralMatches   thrpt   21    4427557.812 ±   290238.935  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringNoMatch         thrpt   21  195470162.926 ±  8848838.744  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringOneMatch        thrpt   21   11076156.521 ±   944743.990  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringSeveralMatches  thrpt   21    6587230.845 ±   589532.187  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringNoMatch                     thrpt   21    5562892.397 ±   485870.574  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringOneMatch                    thrpt   21    1779604.879 ±    62660.787  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringSeveralMatches              thrpt   21    1373842.844 ±    37593.982  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringNoMatch                    thrpt   21    7345620.559 ±   261438.288  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringOneMatch                   thrpt   21    4329321.076 ±    63065.136  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringSeveralMatches             thrpt   21    2866999.696 ±    46869.996  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringNoMatch                 thrpt   21   30699600.817 ±   444331.453  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringOneMatch                thrpt   21    9620813.037 ±   800151.267  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringSeveralMatches          thrpt   21    5003350.550 ±   108551.326  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringNoMatch                thrpt   21  206886806.152 ±  3385778.318  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringOneMatch               thrpt   21   19054271.049 ±   592829.497  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringSeveralMatches         thrpt   21    9453507.916 ±   181412.462  ops/s
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
