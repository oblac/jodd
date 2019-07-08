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

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class InstancesInjector {

	private final MadvocScope madvocScope;

	public InstancesInjector(final MadvocScope bindedScope) {
		this.madvocScope = bindedScope;
	}

	public void inject(final ActionRequest actionRequest, final Targets targets) {
		final HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		final HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();

		targets.forEachTargetAndIn(madvocScope, (target, in) -> {
			final Class inType = in.type();

			Object value = null;

			if (inType == HttpServletRequest.class) {
				value = servletRequest;
			} else if (inType == ServletRequest.class) {
				value = servletRequest;
			} else if (inType == HttpServletResponse.class) {
				value = servletResponse;
			} else if (inType == ServletResponse.class) {
				value = servletResponse;
			} else if (inType == HttpSession.class) {
				value = servletRequest.getSession();
			} else if (inType == ServletContext.class) {
				value = servletRequest.getServletContext();
			} else if (inType == AsyncContext.class) {
				value = servletRequest.getAsyncContext();
			} else if (inType == ActionRequest.class) {
				value = actionRequest;
			}

			if (value != null) {
				target.writeValue(in, value, true);
			}
		});

	}

	public void inject(final ServletContext servletContext, final Targets targets) {
		targets.forEachTargetAndIn(madvocScope, (target, in) -> {
			final Class inType = in.type();

			Object value = null;

			if (inType == ServletContext.class) {
				value = servletContext;
			}

			if (value != null) {
				target.writeValue(in, value, true);
			}
		});

	}


	public boolean isUnusedType(final Class inType) {
		if (inType == HttpServletRequest.class) {
			return false;
		} else if (inType == ServletRequest.class) {
			return false;
		} else if (inType == HttpServletResponse.class) {
			return false;
		} else if (inType == ServletResponse.class) {
			return false;
		} else if (inType == HttpSession.class) {
			return false;
		} else if (inType == ServletContext.class) {
			return false;
		} else if (inType == AsyncContext.class) {
			return false;
		} else if (inType == ActionRequest.class) {
			return false;
		}

		return false;
	}
}
