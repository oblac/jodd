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
 * Benchmark for encoding and decoding base32 - data.<br/>
 * Following <tt>encode</tt>-methods will be compared:
 * <ol>
 *     <li>{@link org.apache.commons.codec.binary.Base32#encodeToString(byte[])}</li>
 *     <li>{@link Base32#encode(byte[])}</li>
 * </ol>
 *
 * And following <tt>decode</tt>-methods will be compared:
 * <ol>
 *     <li>{@link org.apache.commons.codec.binary.Base32#decode(String)}</li>
 *     <li>{@link Base32#decode(String)}</li>
 * </ol>
 *
 * <p>
 * Run:
 * <code>
 * gw :jodd-core:Base32Benchmark
 * </code>
 * </p>
 *
 * Results:
 * <pre>
 * Benchmark                                                    Mode  Cnt          Score       Error    Units
 * Base32Benchmark.decode_Apache_Base32                        thrpt   10    174665,214 ±     6591,054  ops/s
 * Base32Benchmark.decode_Jodd_Base32                          thrpt   10    823002,724 ±    52784,837  ops/s
 * Base32Benchmark.encode_Apache_Base32                        thrpt   10    216608,267 ±     7159,349  ops/s
 * Base32Benchmark.encode_Jodd_Base32                          thrpt   10    373314,481 ±    19277,663  ops/s
 * </pre>
 */
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class Base32Benchmark {

    private byte[] to_be_encoded ;
    private String to_be_decoded ;

	@Setup
	public void prepare() throws UnsupportedEncodingException {
		to_be_encoded = "Jodd is set of Java microframeworks, tools and utilities, under 1.7 MB. We believe in common sense to make things simple, but not simpler. Get things done! Make your ideas! Kickstart your startup! And enjoy the coding.".getBytes("ISO-8859-1");
		to_be_decoded = "JJXWIZBANFZSA43FOQQG6ZRAJJQXMYJANVUWG4TPMZZGC3LFO5XXE23TFQQHI33PNRZSAYLOMQQHK5DJNRUXI2LFOMWCA5LOMRSXEIBRFY3SATKCFYQFOZJAMJSWY2LFOZSSA2LOEBRW63LNN5XCA43FNZZWKIDUN4QG2YLLMUQHI2DJNZTXGIDTNFWXA3DFFQQGE5LUEBXG65BAONUW24DMMVZC4ICHMV2CA5DINFXGO4ZAMRXW4ZJBEBGWC23FEB4W65LSEBUWIZLBOMQSAS3JMNVXG5DBOJ2CA6LPOVZCA43UMFZHI5LQEEQEC3TEEBSW42TPPEQHI2DFEBRW6ZDJNZTS4";
	}

    // ----------------------------------------------------------------------- Apache Commons Codec - Base32

    @Benchmark
    public String encode_Apache_Base32() {
        return new org.apache.commons.codec.binary.Base32(false).encodeAsString(to_be_encoded);
    }

	@Benchmark
	public byte[] decode_Apache_Base32() {
		return new org.apache.commons.codec.binary.Base32(false).decode(to_be_decoded);
	}

    // ----------------------------------------------------------------------- Jodd Base32

    @Benchmark
    public String encode_Jodd_Base32() {
		return Base32.encode(to_be_encoded);
	}

	@Benchmark
	public byte[] decode_Jodd_Base32() {
		return Base32.decode(to_be_decoded);
	}

}