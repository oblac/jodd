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

package jodd.json.impl;

import jodd.json.JsonContext;

import java.lang.reflect.Array;

/**
 * Arrays serializer. May be overridden for specific types for better performances.
 */
public class ArraysJsonSerializer<K> extends ValueJsonSerializer<Object> {

	/**
	 * Returns array's length.
	 */
	protected int getLength(final K[] array) {
		return Array.getLength(array);
	}

	/**
	 * Returns array's element at given index.
	 */
	protected K get(final K[] array, final int index) {
		return (K) Array.get(array, index);
	}

	@Override
	public void serializeValue(final JsonContext jsonContext, final Object array) {
		final int length = getLength((K[]) array);

		if (length == 0 && jsonContext.isExcludeEmpty()) {
			return;
		}

		jsonContext.writeOpenArray();

		int count = 0;

		for (int i = 0; i < length; i++) {
			if (count > 0) {
				jsonContext.writeComma();
			}

			if (jsonContext.serialize(get((K[]) array, i))) {
				count++;
			}
		}

		jsonContext.writeCloseArray();
	}
}