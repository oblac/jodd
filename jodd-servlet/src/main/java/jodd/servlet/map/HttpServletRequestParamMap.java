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

package jodd.servlet.map;

import jodd.util.CollectionUtil;
import jodd.util.collection.StringKeyedMapAdapter;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * Map backed by the Servlet HTTP request parameters for accessing request local attributes.
 * Map is read-only as setting or removing parameters is not possible.
 */
public class HttpServletRequestParamMap extends StringKeyedMapAdapter {

	/**
	 * The wrapped HTTP request.
	 */
	private final HttpServletRequest request;

	/**
	 * Create a new map wrapping the attributes of given request.
	 */
	public HttpServletRequestParamMap(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	protected Object getAttribute(String key) {
		String[] values = request.getParameterValues(key);
		if (values == null) {
			return null;
		}
		if (values.length == 1) {
			return values[0];
		}
		return values;
	}

	@Override
	protected void setAttribute(String key, Object value) {
		throw new UnsupportedOperationException("Read-only map");
	}

	@Override
	protected void removeAttribute(String key) {
		throw new UnsupportedOperationException("Read-only map");
	}

	@Override
	@SuppressWarnings({"unchecked"})
	protected Iterator<String> getAttributeNames() {
		return CollectionUtil.asIterator(request.getParameterNames());
	}

}