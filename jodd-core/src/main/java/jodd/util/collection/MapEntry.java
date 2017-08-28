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

package jodd.util.collection;

import java.util.Map;

/**
 * Default {@code Map.Entry} implementation. Both key and the value can be
 * modified.
 */
public class MapEntry<K, V> implements Map.Entry<K, V> {

	public static <T, R> MapEntry<T, R> create(T key, R value) {
		return new MapEntry<>(key, value);
	}

	public static <T, R> MapEntry<T, R> createUnmodifiable(T key, R value) {
		return new UnmodifiableMapEntry<>(key, value);
	}

	public MapEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	// ---------------------------------------------------------------- methods

	private K key;
	private V value;

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		this.value = value;
		return this.value;
	}

	public K setKey(K key) {
		this.key = key;
		return this.key;
	}

	// ---------------------------------------------------------------- hash/equals

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Map.Entry)) {
			return false;
		}

		Map.Entry entry = (Map.Entry) obj;
		return ((key == null) ?
			(entry.getKey() == null) :
			key.equals(entry.getKey())) && ((value == null) ?
				(entry.getValue() == null) :
				value.equals(entry.getValue()));
	}

	@Override
	public int hashCode() {
		return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
	}

}
