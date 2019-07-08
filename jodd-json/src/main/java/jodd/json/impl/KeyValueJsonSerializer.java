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
import jodd.json.Path;
import jodd.util.StringPool;

/**
 * Key value JSON serializer.
 */
public abstract class KeyValueJsonSerializer<T> extends ValueJsonSerializer<T> {

	/**
	 * Serializes key and a value.
	 */
	protected int serializeKeyValue(final JsonContext jsonContext, final Path currentPath, final Object key, final Object value, int count) {
		if ((value == null) && jsonContext.isExcludeNulls()) {
			return count;
		}

		if (key != null) {
			currentPath.push(key.toString());
		} else {
			currentPath.push(StringPool.NULL);
		}

		// check if we should include the field

		boolean include = true;

		if (value != null) {

			// + all collections are not serialized by default

			include = jsonContext.matchIgnoredPropertyTypes(value.getClass(), false, include);

			// + path queries: excludes/includes

			include = jsonContext.matchPathToQueries(include);
		}

		// done

		if (!include) {
			currentPath.pop();
			return count;
		}

		if (key == null) {
			jsonContext.pushName(null, count > 0);
		} else {
			jsonContext.pushName(key.toString(), count > 0);
		}

		jsonContext.serialize(value);

		if (jsonContext.isNamePopped()) {
			count++;
		}

		currentPath.pop();
		return count;
	}
}