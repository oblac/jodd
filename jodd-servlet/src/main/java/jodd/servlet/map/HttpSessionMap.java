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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * Map backed by the Servlet session for accessing session scoped attributes.
 */
public class HttpSessionMap extends StringKeyedMapAdapter {

	private final HttpSession session;

	public HttpSessionMap(HttpSession session) {
		this.session = session;
	}

	public HttpSessionMap(HttpServletRequest request) {
		this(request.getSession());
	}

	@Override
	protected Object getAttribute(String key) {
		return session.getAttribute(key);
	}

	@Override
	protected void setAttribute(String key, Object value) {
		session.setAttribute(key, value);
	}

	@Override
	protected void removeAttribute(String key) {
		session.removeAttribute(key);
	}

	@Override
	@SuppressWarnings({"unchecked"})
	protected Iterator<String> getAttributeNames() {
		return CollectionUtil.asIterator(session.getAttributeNames());
	}

}