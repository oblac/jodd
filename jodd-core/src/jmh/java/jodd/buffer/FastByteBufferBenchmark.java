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

package jodd.buffer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

/**
 Benchmark                         (size)   Mode  Cnt      Score      Error   Units
 FastBufferBenchmark.fastBuffer         1  thrpt    3  79324.336 ± 5717.098  ops/ms
 FastBufferBenchmark.fastBuffer        33  thrpt    3  43391.056 ± 1624.374  ops/ms
 FastBufferBenchmark.fastBuffer        64  thrpt    3  30362.627 ± 7105.192  ops/ms
 FastBufferBenchmark.fastBuffer        65  thrpt    3   7308.389 ±  567.833  ops/ms
 FastBufferBenchmark.fastBuffer       128  thrpt    3   4466.231 ±  397.316  ops/ms
 FastBufferBenchmark.fastBuffer       129  thrpt    3   3984.090 ±  436.589  ops/ms
 FastBufferBenchmark.fastBuffer       256  thrpt    3   2203.161 ±  102.874  ops/ms
 FastBufferBenchmark.fastBuffer       257  thrpt    3   2078.249 ±  240.826  ops/ms
 FastBufferBenchmark.fastBuffer       512  thrpt    3   1125.737 ±  152.728  ops/ms
 FastBufferBenchmark.fastBuffer       513  thrpt    3   1008.258 ±  153.849  ops/ms
 FastBufferBenchmark.fastBuffer      1024  thrpt    3    553.377 ±  111.084  ops/ms
 FastBufferBenchmark.fastBuffer      2048  thrpt    3    273.436 ±   43.738  ops/ms
 FastBufferBenchmark.outputStream       1  thrpt    3  68489.943 ± 4079.964  ops/ms
 FastBufferBenchmark.outputStream      33  thrpt    3   9579.040 ±  373.262  ops/ms
 FastBufferBenchmark.outputStream      64  thrpt    3   5876.755 ±  958.445  ops/ms
 FastBufferBenchmark.outputStream      65  thrpt    3   5171.245 ±  488.868  ops/ms
 FastBufferBenchmark.outputStream     128  thrpt    3   4008.639 ±  260.015  ops/ms
 FastBufferBenchmark.outputStream     129  thrpt    3   3687.005 ±  148.731  ops/ms
 FastBufferBenchmark.outputStream     256  thrpt    3   2074.286 ±   96.801  ops/ms
 FastBufferBenchmark.outputStream     257  thrpt    3   1925.055 ±  219.249  ops/ms
 FastBufferBenchmark.outputStream     512  thrpt    3   1067.137 ±  132.376  ops/ms
 FastBufferBenchmark.outputStream     513  thrpt    3    967.751 ±  100.224  ops/ms
 FastBufferBenchmark.outputStream    1024  thrpt    3    532.129 ±   41.634  ops/ms
 FastBufferBenchmark.outputStream    2048  thrpt    3    261.490 ±   22.416  ops/ms
 */
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 3)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.MILLISECONDS)
public class FastByteBufferBenchmark {

	@Param({"1", "33", "64", "65", "128", "129", "256", "257", "512", "513", "1024", "2048"})
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
