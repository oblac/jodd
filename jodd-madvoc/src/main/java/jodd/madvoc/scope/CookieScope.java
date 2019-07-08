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

package jodd.madvoc.scope;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.config.Targets;
import jodd.servlet.ServletUtil;
import jodd.util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie scope.
 */
public class CookieScope implements MadvocScope {

	@Override
	public void inject(final ActionRequest actionRequest, final Targets targets) {
		final HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		targets.forEachTargetAndIn(this, (target, in) -> {
			Object value = null;

			if (in.type() == Cookie.class) {
				// get single cookie
				final String cookieName = StringUtil.uncapitalize(in.name());
				value = ServletUtil.getCookie(servletRequest, cookieName);
			}
			else if (in.type().isArray()) {
				if (in.type().getComponentType().equals(Cookie.class)) {
					if (StringUtil.isEmpty(in.name())) {
						// get all cookies
						value = servletRequest.getCookies();
					} else {
						// get all cookies by name
						value = ServletUtil.getAllCookies(servletRequest, in.name());
					}
				}
			}

			if (value != null) {
				target.writeValue(in, value, true);
			}
		});
	}

	@Override
	public void inject(final ServletContext servletContext, final Targets targets) {
	}

	@Override
	public void inject(final Targets targets) {
	}

	@Override
	public void outject(final ActionRequest actionRequest, final Targets targets) {
		final HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();

		targets.forEachTargetAndOut(this, (target, out) -> {
			final Cookie cookie = (Cookie) target.readValue(out);
			if (cookie != null) {
				servletResponse.addCookie(cookie);
			}
		});
	}
}
