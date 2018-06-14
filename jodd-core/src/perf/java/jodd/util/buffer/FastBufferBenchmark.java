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

package jodd.util.buffer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.io.ByteArrayOutputStream;


/**
 Benchmark                         (size)   Mode  Cnt         Score         Error  Units
 FastBufferBenchmark.fastBuffer         1  thrpt    3  77527235.304 ± 4442746.838  ops/s
 FastBufferBenchmark.fastBuffer        64  thrpt    3  30006630.597 ± 1476331.339  ops/s
 FastBufferBenchmark.fastBuffer        65  thrpt    3   7081082.296 ± 1163498.879  ops/s
 FastBufferBenchmark.fastBuffer       129  thrpt    3   3891205.544 ±  318860.315  ops/s
 FastBufferBenchmark.outputStream       1  thrpt    3  66389091.918 ± 3225304.782  ops/s
 FastBufferBenchmark.outputStream      64  thrpt    3   5708091.375 ±  344353.783  ops/s
 FastBufferBenchmark.outputStream      65  thrpt    3   4986598.531 ±  483090.653  ops/s
 FastBufferBenchmark.outputStream     129  thrpt    3   3557669.425 ±  143865.958  ops/s
 */
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 3)
@State(Scope.Benchmark)
public class FastBufferBenchmark {

	//@Param({"1", "64", "65", "129"})
	@Param({"2049"})
	public int size;

	@Benchmark
	public byte[] fastBuffer() {
		final FastByteBuffer fastBuffer = new FastByteBuffer();
		for (int i = 0; i < size; i++) {
			fastBuffer.append((byte) i);
		}
		return fastBuffer.toArray();
	}

	@Benchmark
	public byte[] outputStream() {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < size; i++) {
			baos.write(i);
		}
		return baos.toByteArray();
	}

//	@Benchmark
//	public Byte[] arrayList() {
//		final ArrayList<Byte> arrayList = new ArrayList<>();
//		for (int i = 0; i < size; i++) {
//			arrayList.add((byte) i);
//		}
//		return arrayList.toArray(new Byte[0]);
//	}

}
