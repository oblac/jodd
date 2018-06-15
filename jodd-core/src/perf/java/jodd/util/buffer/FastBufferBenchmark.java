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

/*
 1
 FastBufferBenchmark.fastBuffer         1  thrpt    2  78337.407          ops/ms
 FastBufferBenchmark.outputStream       1  thrpt    2  66574.613          ops/ms
 64
 FastBufferBenchmark.fastBuffer        64  thrpt    2  29520.367          ops/ms
 FastBufferBenchmark.outputStream      64  thrpt    2   5702.094          ops/ms
 65
 FastBufferBenchmark.fastBuffer        65  thrpt    2  7598.382          ops/ms
 FastBufferBenchmark.outputStream      65  thrpt    2  5040.590          ops/ms
 128
 FastBufferBenchmark.fastBuffer       128  thrpt    2  4668.539          ops/ms
 FastBufferBenchmark.outputStream     128  thrpt    2  3898.102          ops/ms
 129
 FastBufferBenchmark.fastBuffer       129  thrpt    2  4035.519          ops/ms
 FastBufferBenchmark.outputStream     129  thrpt    2  3487.195          ops/ms
 256
 FastBufferBenchmark.fastBuffer       256  thrpt    2  2300.108          ops/ms
 FastBufferBenchmark.outputStream     256  thrpt    2  2012.312          ops/ms
 257
 FastBufferBenchmark.fastBuffer       257  thrpt    2  2177.609          ops/ms
 FastBufferBenchmark.outputStream     257  thrpt    2  1878.996          ops/ms
 512
 FastBufferBenchmark.fastBuffer       512  thrpt    2  1184.921          ops/ms
 FastBufferBenchmark.outputStream     512  thrpt    2  1034.150          ops/ms
 513
 FastBufferBenchmark.fastBuffer       513  thrpt    2  1067.237          ops/ms
 FastBufferBenchmark.outputStream     513  thrpt    2   949.810          ops/ms
 1024
 FastBufferBenchmark.fastBuffer      1024  thrpt    2  588.293          ops/ms
 FastBufferBenchmark.outputStream    1024  thrpt    2  519.904          ops/ms

 ...

 1 MB
 FastBufferBenchmark.fastBuffer    1048576  thrpt    2  24842.200          ops/min
 FastBufferBenchmark.outputStream  1048576  thrpt    2  24676.125          ops/min
 128 MB
 FastBufferBenchmark.fastBuffer    157810688  thrpt    2  126.644          ops/min
 FastBufferBenchmark.outputStream  157810688  thrpt    2  124.815          ops/min
 128 MB +1
 FastBufferBenchmark.fastBuffer    157810689  thrpt    2  130.189          ops/min
 FastBufferBenchmark.outputStream  157810689  thrpt    2  132.132          ops/min
 130 MB
 FastBufferBenchmark.fastBuffer    136314880  thrpt    2  149.155          ops/min
 FastBufferBenchmark.outputStream  136314880  thrpt    2  148.231          ops/min
 150 MB
 FastBufferBenchmark.fastBuffer    157286400  thrpt    2  135.111          ops/min
 FastBufferBenchmark.outputStream  157286400  thrpt    2  131.013          ops/min
 200 MB
 FastBufferBenchmark.fastBuffer    209715200  thrpt    2  106.592          ops/min
 FastBufferBenchmark.outputStream  209715200  thrpt    2  103.280          ops/min
 256 MB
 FastBufferBenchmark.fastBuffer    268435456  thrpt    2  84.519          ops/min
 FastBufferBenchmark.outputStream  268435456  thrpt    2  55.932          ops/min
 300 MB
 FastBufferBenchmark.fastBuffer    314572800  thrpt    2  58.508          ops/min
 FastBufferBenchmark.outputStream  314572800  thrpt    2  60.417          ops/min
 400 MB
 FastBufferBenchmark.fastBuffer    419430400  thrpt    2  35.836          ops/min
 FastBufferBenchmark.outputStream  419430400  thrpt    2  48.079          ops/min
 512 MB
 FastBufferBenchmark.fastBuffer    536870912  thrpt    2  41.725          ops/min
 FastBufferBenchmark.outputStream  536870912  thrpt    2  28.819          ops/min
 */

/**
 * This is a benchmark for previous implementation.
 */
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 2)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.MINUTES)
public class FastBufferBenchmark {

	@Param({"419430400"})
	public int size;

//	@Benchmark
//	public byte[] fastBuffer() {
//		final FastByteBuffer fastBuffer = new FastByteBuffer();
//		for (int i = 0; i < size; i++) {
//			fastBuffer.append((byte) i);
//		}
//		return fastBuffer.toArray();
//	}

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
