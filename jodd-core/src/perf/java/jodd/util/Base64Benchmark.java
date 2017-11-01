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
import org.openjdk.jmh.infra.Blackhole;

import java.io.UnsupportedEncodingException;

/**
 * Benchmark for encoding and decoding base64 - data.<br/>
 * Following <tt>encode</tt>-methods will be compared:
 * <ol>
 *     <li>{@link java.util.Base64.Encoder#encodeToString(byte[])}</li>
 *     <li>{@link org.apache.commons.codec.binary.Base64#encodeToString(byte[])}</li>
 *     <li>{@link Base64#encodeToString(byte[])}</li>
 * </ol>
 *
 * And following <tt>decode</tt>-methods will be compared:
 * <ol>
 *     <li>{@link java.util.Base64.Decoder#decode(String)}</li>
 *     <li>{@link org.apache.commons.codec.binary.Base64#decode(String)}</li>
 *     <li>{@link Base64#decode(String)}</li>
 * </ol>
 *
 * <p>
 * Run:
 * <code>
 * gw :jodd-core:perf -PBase64Benchmark
 * </code>
 * </p>
 *
 * Results:
 * <pre>
 * Benchmark                                                                      Mode  Cnt          Score         Error  Units
 * Base64Benchmark.decode_Apache_Base64                                          thrpt   10     428710.504 ±   19254.182  ops/s
 * Base64Benchmark.decode_Java_Base64                                            thrpt   10    1247073.684 ±   15426.300  ops/s
 * Base64Benchmark.decode_Jodd_Base64                                            thrpt   10    2102674.827 ±   43933.923  ops/s
 * Base64Benchmark.encode_Apache_Base64                                          thrpt   10     425022.574 ±    6719.127  ops/s
 * Base64Benchmark.encode_Java_Base64                                            thrpt   10    2317106.258 ±   48035.465  ops/s
 * Base64Benchmark.encode_Jodd_Base64                                            thrpt   10    2308577.816 ±  111663.488  ops/s
 * CharUtilIWhitespaceBenchmark.isWhitespace_Java                                thrpt   10   14198643.215 ±  132409.477  ops/s
 * CharUtilIWhitespaceBenchmark.isWhitespace_Jodd                                thrpt   10  340392811.038 ± 5584431.305  ops/s
 * StringBandBenchmark.string2                                                   thrpt   10   35891067.323 ± 1044617.014  ops/s
 * StringBandBenchmark.string3                                                   thrpt   10   16578168.807 ± 1528910.415  ops/s
 * StringBandBenchmark.stringBand2                                               thrpt   10   26738669.712 ± 2399655.941  ops/s
 * StringBandBenchmark.stringBand3                                               thrpt   10   17382025.209 ± 8201893.387  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringNoMatch          thrpt   21   30452115.098 ±  673893.391  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringOneMatch         thrpt   21    7939333.083 ±  159884.176  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringSeveralMatches   thrpt   21    4572341.549 ±  190924.651  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringNoMatch         thrpt   21  200849811.354 ± 6004094.222  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringOneMatch        thrpt   21   12733470.739 ±  204528.150  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringSeveralMatches  thrpt   21    7037310.309 ±  240382.430  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringNoMatch                     thrpt   21    5985542.910 ±  145759.366  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringOneMatch                    thrpt   21    1760430.973 ±   61879.332  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringSeveralMatches              thrpt   21    1338152.084 ±   38966.154  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringNoMatch                    thrpt   21    6944636.309 ±  283722.384  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringOneMatch                   thrpt   21    4071359.788 ±  166071.428  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringSeveralMatches             thrpt   21    2660695.634 ±  247642.047  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringNoMatch                 thrpt   21   30705952.332 ±  441294.722  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringOneMatch                thrpt   21    9057931.761 ±  411462.754  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringSeveralMatches          thrpt   21    4688956.630 ±  185023.595  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringNoMatch                thrpt   21  203959018.661 ± 5308589.795  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringOneMatch               thrpt   21   17706892.816 ± 1073079.702  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringSeveralMatches         thrpt   21    8975422.713 ±  225388.842  ops/s
 * </pre>
 */
@Fork(1)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class Base64Benchmark {

    private byte[] to_be_encoded ;
    private String to_be_decoded ;

	@Setup
	public void prepare() throws UnsupportedEncodingException {
		to_be_encoded = "Jodd is set of Java microframeworks, tools and utilities, under 1.7 MB. We believe in common sense to make things simple, but not simpler. Get things done! Make your ideas! Kickstart your startup! And enjoy the coding.".getBytes("UTF-8");
		to_be_decoded = "Sm9kZCBpcyBzZXQgb2YgSmF2YSBtaWNyb2ZyYW1ld29ya3MsIHRvb2xzIGFuZCB1dGlsaXRpZXMsIHVuZGVyIDEuNyBNQi4gV2UgYmVsaWV2ZSBpbiBjb21tb24gc2Vuc2UgdG8gbWFrZSB0aGluZ3Mgc2ltcGxlLCBidXQgbm90IHNpbXBsZXIuIEdldCB0aGluZ3MgZG9uZSEgTWFrZSB5b3VyIGlkZWFzISBLaWNrc3RhcnQgeW91ciBzdGFydHVwISBBbmQgZW5qb3kgdGhlIGNvZGluZy4=";
	}

    // ----------------------------------------------------------------------- Java Base64
    
    @Benchmark
    public String encode_Java_Base64() {
        return java.util.Base64.getEncoder().encodeToString(to_be_encoded);
    }

	@Benchmark
	public byte[] decode_Java_Base64() {
		return java.util.Base64.getDecoder().decode(to_be_decoded);
	}

    // ----------------------------------------------------------------------- Apache Commons Codec - Base64
    
    @Benchmark
    public String encode_Apache_Base64() {
        return org.apache.commons.codec.binary.Base64.encodeBase64String(to_be_encoded);
    }

	@Benchmark
	public byte[] decode_Apache_Base64(Blackhole blackhole) {
		return org.apache.commons.codec.binary.Base64.decodeBase64(to_be_decoded);
	}

    // ----------------------------------------------------------------------- Jodd Base64

    @Benchmark
    public String encode_Jodd_Base64(Blackhole blackhole) {
        return jodd.util.Base64.encodeToString(to_be_encoded, false);
    }

	@Benchmark
	public byte[] decode_Jodd_Base64(Blackhole blackhole) {
		return jodd.util.Base64.decode(to_be_decoded);
	}

}