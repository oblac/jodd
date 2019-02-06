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

import java.io.CharArrayWriter;
import java.util.concurrent.TimeUnit;

/**
 FastCharBufferBenchmark.charArrayWriter       1  thrpt    3  61484.761 ± 8391.668  ops/ms
 FastCharBufferBenchmark.charArrayWriter      33  thrpt    3   5835.162 ±  524.475  ops/ms
 FastCharBufferBenchmark.charArrayWriter      64  thrpt    3   3542.045 ±  122.295  ops/ms
 FastCharBufferBenchmark.charArrayWriter      65  thrpt    3   3227.802 ±  386.257  ops/ms
 FastCharBufferBenchmark.charArrayWriter     128  thrpt    3   1825.567 ±  132.566  ops/ms
 FastCharBufferBenchmark.charArrayWriter     129  thrpt    3   1681.234 ±  151.375  ops/ms
 FastCharBufferBenchmark.charArrayWriter     256  thrpt    3    912.488 ±   38.210  ops/ms
 FastCharBufferBenchmark.charArrayWriter     257  thrpt    3    830.470 ±   73.738  ops/ms
 FastCharBufferBenchmark.charArrayWriter     512  thrpt    3    454.026 ±   18.264  ops/ms
 FastCharBufferBenchmark.charArrayWriter     513  thrpt    3    405.199 ±   14.203  ops/ms
 FastCharBufferBenchmark.charArrayWriter    1024  thrpt    3    255.465 ±   16.976  ops/ms
 FastCharBufferBenchmark.charArrayWriter    2048  thrpt    3    127.222 ±    5.253  ops/ms
 FastCharBufferBenchmark.fastBuffer            1  thrpt    3  48499.781 ± 3417.869  ops/ms
 FastCharBufferBenchmark.fastBuffer           33  thrpt    3  32205.931 ± 4606.799  ops/ms
 FastCharBufferBenchmark.fastBuffer           64  thrpt    3  24026.940 ± 2260.104  ops/ms
 FastCharBufferBenchmark.fastBuffer           65  thrpt    3   5153.764 ±  394.257  ops/ms
 FastCharBufferBenchmark.fastBuffer          128  thrpt    3   3852.993 ±  186.961  ops/ms
 FastCharBufferBenchmark.fastBuffer          129  thrpt    3   3567.835 ±  666.075  ops/ms
 FastCharBufferBenchmark.fastBuffer          256  thrpt    3   1983.722 ±   47.144  ops/ms
 FastCharBufferBenchmark.fastBuffer          257  thrpt    3   1670.929 ±  181.778  ops/ms
 FastCharBufferBenchmark.fastBuffer          512  thrpt    3    981.308 ±   24.554  ops/ms
 FastCharBufferBenchmark.fastBuffer          513  thrpt    3    800.657 ±   19.003  ops/ms
 FastCharBufferBenchmark.fastBuffer         1024  thrpt    3    472.392 ±   24.202  ops/ms
 FastCharBufferBenchmark.fastBuffer         2048  thrpt    3    224.634 ±    5.223  ops/ms

TOSTRING() with StringBuilder
 FastCharBufferBenchmark.stringBuilder       1  thrpt    3  68626.822 ± 4664.250  ops/ms
 FastCharBufferBenchmark.stringBuilder      33  thrpt    3   9249.230 ± 1034.967  ops/ms
 FastCharBufferBenchmark.stringBuilder      64  thrpt    3   5094.500 ±  446.172  ops/ms
 FastCharBufferBenchmark.stringBuilder      65  thrpt    3   5017.372 ±  142.879  ops/ms
 FastCharBufferBenchmark.stringBuilder     128  thrpt    3   3378.681 ±  358.251  ops/ms
 FastCharBufferBenchmark.stringBuilder     129  thrpt    3   3351.333 ±  287.815  ops/ms
 FastCharBufferBenchmark.stringBuilder     256  thrpt    3   1764.191 ±   81.100  ops/ms
 FastCharBufferBenchmark.stringBuilder     257  thrpt    3   1767.445 ±   73.462  ops/ms
 FastCharBufferBenchmark.stringBuilder     512  thrpt    3    892.306 ±   34.067  ops/ms
 FastCharBufferBenchmark.stringBuilder     513  thrpt    3    891.382 ±   85.039  ops/ms
 FastCharBufferBenchmark.stringBuilder    1024  thrpt    3    460.738 ±   69.925  ops/ms
 FastCharBufferBenchmark.stringBuilder    2048  thrpt    3    231.814 ±   10.881  ops/ms
 */
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 3)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.MILLISECONDS)
public class FastCharBufferBenchmark {

	@Param({"1", "33", "64", "65", "128", "129", "256", "257", "512", "513", "1024", "2048"})
	public int size;

	@Benchmark
	public char[] fastBuffer() {
		final FastCharBuffer fastBuffer = new FastCharBuffer();
		for (int i = 0; i < size; i++) {
			fastBuffer.append((char) i);
		}
		return fastBuffer.toArray();
	}

	@Benchmark
	public char[] charArrayWriter() {
		final CharArrayWriter charArrayWriter = new CharArrayWriter();
		for (int i = 0; i < size; i++) {
			charArrayWriter.append((char)i);
		}
		return charArrayWriter.toCharArray();
	}

}
