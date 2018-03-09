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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Random;


/**
 TypeCacheBenchmark.map          thrpt   20  32709.552 ± 612.549  ops/s
 TypeCacheBenchmark.syncMap      thrpt   20  32646.570 ± 483.059  ops/s
 TypeCacheBenchmark.weakMap      thrpt   20  31566.891 ± 361.375  ops/s
 TypeCacheBenchmark.weakSyncMap  thrpt   20  22311.518 ± 342.584  ops/s
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
		Throwable.class, Thread.class, System.class
	};

	private TypeCache<String> map = TypeCache.create(TypeCache.Implementation.MAP);
	private TypeCache<String> syncMap = TypeCache.create(TypeCache.Implementation.SYNC_MAP);
	private TypeCache<String> weakMap = TypeCache.create(TypeCache.Implementation.WEAK);
	private TypeCache<String> weakSyncMap = TypeCache.create(TypeCache.Implementation.SYNC_WEAK);
	private int[] indexes = new int[1024];

	@Setup
	public void prepare() {
		for (Class type : TYPES) {
			map.put(type, type.getName());
			syncMap.put(type, type.getName());
			weakMap.put(type, type.getName());
			weakSyncMap.put(type, type.getName());
		}

		Random rnd = new Random();
		for (int i = 0; i < 1024; i++) {
			indexes[i] = rnd.nextInt(TYPES.length);
		}
	}

	@Benchmark
	public StringBuilder map() {
		StringBuilder sb = new StringBuilder();
		for (int index : indexes) {
			sb.append(map.get(TYPES[index]));
		}
		return sb;
	}

	@Benchmark
	public StringBuilder syncMap() {
		StringBuilder sb = new StringBuilder();
		for (int index : indexes) {
			sb.append(syncMap.get(TYPES[index]));
		}
		return sb;
	}

	@Benchmark
	public StringBuilder weakMap() {
		StringBuilder sb = new StringBuilder();
		for (int index : indexes) {
			sb.append(weakMap.get(TYPES[index]));
		}
		return sb;
	}

	@Benchmark
	public StringBuilder weakSyncMap() {
		StringBuilder sb = new StringBuilder();
		for (int index : indexes) {
			sb.append(weakSyncMap.get(TYPES[index]));
		}
		return sb;
	}

}
