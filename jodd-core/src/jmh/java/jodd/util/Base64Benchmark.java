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
 * gw :jodd-core:Base64Benchmark
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
	public byte[] decode_Apache_Base64() {
		return org.apache.commons.codec.binary.Base64.decodeBase64(to_be_decoded);
	}

    // ----------------------------------------------------------------------- Jodd Base64

    @Benchmark
    public String encode_Jodd_Base64() {
        return jodd.util.Base64.encodeToString(to_be_encoded, false);
    }

	@Benchmark
	public byte[] decode_Jodd_Base64() {
		return jodd.util.Base64.decode(to_be_decoded);
	}

}
