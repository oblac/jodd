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

package jodd.cache;

import jodd.buffer.FastCharBuffer;
import jodd.io.FastByteArrayOutputStream;
import jodd.io.FastCharArrayWriter;
import jodd.io.FileNameUtil;
import jodd.io.IOUtil;
import jodd.io.NetUtil;
import jodd.io.PathUtil;
import jodd.io.ZipUtil;
import jodd.mutable.MutableBoolean;
import jodd.mutable.MutableByte;
import jodd.mutable.MutableInteger;
import jodd.mutable.MutableLong;
import jodd.util.TypeCache;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 TypeCacheBenchmark.map            thrpt   20  47135.079 ±  968.012  ops/s
 TypeCacheBenchmark.simpleHashMap  thrpt   20  45526.617 ±  797.989  ops/s
 TypeCacheBenchmark.smoothieMap    thrpt   20  39182.106 ±  545.616  ops/s
 TypeCacheBenchmark.syncMap        thrpt   20  40134.180 ± 1308.250  ops/s
 TypeCacheBenchmark.timedCache     thrpt   20  13929.643 ±   95.971  ops/s
 TypeCacheBenchmark.weakMap        thrpt   20  36468.661 ± 1612.440  ops/s
 TypeCacheBenchmark.weakSyncMap    thrpt   20  26196.027 ±  252.894  ops/s
*/
@Fork(2)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class TypeCacheBenchmark {

	private static final Class[] TYPES = {
		Long.class, Integer.class, Float.class, Double.class, Byte.class, Short.class, Boolean.class, Enum.class,
		InternalError.class, Math.class, Long.class, Number.class, Object.class, Package.class, Class.class,
		Cloneable.class, ClassLoader.class, Compiler.class, Comparable.class, IllegalArgumentException.class,
		Appendable.class, String.class, AssertionError.class, CharSequence.class, OutOfMemoryError.class,
		ProcessBuilder.class, NullPointerException.class, Void.class, VerifyError.class,
		Throwable.class, Thread.class, System.class, AbstractCacheMap.class, Cache.class, FIFOCache.class,
		FileCache.class, NoCache.class, FastByteArrayOutputStream.class, FastCharArrayWriter.class,
		FileNameUtil.class, NetUtil.class, PathUtil.class, IOUtil.class, ZipUtil.class,
		MutableInteger.class, MutableLong.class, MutableBoolean.class, MutableByte.class
	};

	{
		System.out.println("Total types: " + TYPES.length);
	}

	private static final int TOTAL_READS = 1024;
	private final TypeCache<String> map = TypeCache.<String>create().get();
	private final TypeCache<String> syncMap = TypeCache.<String>create().threadsafe(true).get();
	private final TypeCache<String> weakMap = TypeCache.<String>create().weak(true).get();
	private final TypeCache<String> weakSyncMap = TypeCache.<String>create().weak(true).threadsafe(true).get();
	private final Map<Class, String> smoothieMap = new net.openhft.smoothie.SmoothieMap<>();
	private final Map<Class, String> simpleHashMap = new HashMap<>();
	private final Cache<Class, String> timedCache = new TimedCache<>(0);
	private final int[] indexes = new int[TOTAL_READS];

	@Setup
	public void prepare() {
		for (final Class type : TYPES) {
			final String typeName = type.getName();
			map.put(type, typeName);
			syncMap.put(type, typeName);
			weakMap.put(type, typeName);
			weakSyncMap.put(type, typeName);
			smoothieMap.put(type, typeName);
			simpleHashMap.put(type, typeName);
			timedCache.put(type, typeName);
		}

		final Random rnd = new Random();
		for (int i = 0; i < TOTAL_READS; i++) {
			indexes[i] = rnd.nextInt(TYPES.length);
		}
	}

	// ---------------------------------------------------------------- benchmark

	@Benchmark
	public Object map() {
		final FastCharBuffer sb = new FastCharBuffer();
		for (final int index : indexes) {
			sb.append(map.get(TYPES[index]));
		}
		return sb;
	}

	@Benchmark
	public Object syncMap() {
		final FastCharBuffer sb = new FastCharBuffer();
		for (final int index : indexes) {
			sb.append(syncMap.get(TYPES[index]));
		}
		return sb;
	}

	@Benchmark
	public Object weakMap() {
		final FastCharBuffer sb = new FastCharBuffer();
		for (final int index : indexes) {
			sb.append(weakMap.get(TYPES[index]));
		}
		return sb;
	}

	@Benchmark
	public Object weakSyncMap() {
		final FastCharBuffer sb = new FastCharBuffer();
		for (final int index : indexes) {
			sb.append(weakSyncMap.get(TYPES[index]));
		}
		return sb;
	}

	@Benchmark
	public Object smoothieMap() {
		final FastCharBuffer sb = new FastCharBuffer();
		for (final int index : indexes) {
			sb.append(smoothieMap.get(TYPES[index]));
		}
		return sb;
	}

	@Benchmark
	public Object simpleHashMap() {
		final FastCharBuffer sb = new FastCharBuffer();
		for (final int index : indexes) {
			sb.append(simpleHashMap.get(TYPES[index]));
		}
		return sb;
	}

	@Benchmark
	public Object timedCache() {
		final FastCharBuffer sb = new FastCharBuffer();
		for (final int index : indexes) {
			sb.append(timedCache.get(TYPES[index]));
		}
		return sb;
	}

}
