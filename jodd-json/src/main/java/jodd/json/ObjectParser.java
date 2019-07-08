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

package jodd.json;

import java.util.function.Supplier;

/**
 * Extracted function for parsing object content.
 * This function is called in two periods:
 * <ul>
 *     <li>during parsing</li>
 *     <li>after parsing, in Lazy collections.</li>
 * </ul>
 *
 * This functional method re-use existing parser instance. The only thing we need to preserve are:
 * <ul>
 *     <li>current index,</li>
 *     <li>current path</li>
 * </ul>
 */
class ObjectParser implements Supplier {

	private final int ndx;
	private final Class targetType;
	private final Class keyType;
	private final Class componentType;
	private final JsonParser jsonParser;
	private final Path path;

	ObjectParser(final JsonParser jsonParser, final Class targetType, final Class keyType, final Class componentType) {
		this.jsonParser = jsonParser;

		this.ndx = jsonParser.ndx;
		this.path = jsonParser.path.clone();

		this.targetType = targetType;
		this.keyType = keyType;
		this.componentType = componentType;
	}

	@Override
	public Object get() {
		final int currentNdx = jsonParser.ndx;
		final Path currentPath = jsonParser.path;

		jsonParser.ndx = ndx;
		jsonParser.path = path;

		final Object object = jsonParser.parseObjectContent(targetType, keyType, componentType);

		jsonParser.ndx = currentNdx;
		jsonParser.path = currentPath;

		return object;
	}
}
