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

package jodd.decora;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Special <code>HttpServletRequestWrapper</code> allows filtering of the HTTP headers.
 */
public class DecoraRequestWrapper extends HttpServletRequestWrapper {

	public DecoraRequestWrapper(final HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	/**
	 * Returns <code>null</code> for excluded HTTP headers.
	 */
	@Override
	public String getHeader(final String header) {
		if (isExcluded(header)) {
			return null;
		}
		return super.getHeader(header);
	}

	/**
	 * Returns <code>-1</code> for excluded HTTP headers.
	 */
	@Override
	public long getDateHeader(final String header) {
		if (isExcluded(header)) {
			return -1;
		}
		return super.getDateHeader(header);
	}

	/**
	 * Checks if header name is excluded.
	 */
	protected boolean isExcluded(final String header) {
		return "If-Modified-Since".equalsIgnoreCase(header);
	}

}