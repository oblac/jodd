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

package jodd.madvoc.injector;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * Servlet session scope injector.
 */
public class SessionScopeInjector implements Injector, Outjector {
	private final static ScopeType SCOPE_TYPE = ScopeType.SESSION;

	@Override
	public void inject(ActionRequest actionRequest) {
		Targets targets = actionRequest.getTargets();
		if (!targets.usesScope(SCOPE_TYPE)) {
			return;
		}

		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpSession session = servletRequest.getSession();

		Enumeration attributeNames = session.getAttributeNames();

		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();

			targets.forEachTargetAndInScopes(SCOPE_TYPE, (target, in) -> {
				String name = in.matchedPropertyName(attrName);
				if (name != null) {
					Object attrValue = session.getAttribute(attrName);
					target.writeValue(name, attrValue, true);
				}
			});
		}
	}

	@Override
	public void outject(ActionRequest actionRequest) {
		Targets targets = actionRequest.getTargets();
		if (!targets.usesScope(SCOPE_TYPE)) {
			return;
		}

		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpSession session = servletRequest.getSession();

		targets.forEachTargetAndOutScopes(SCOPE_TYPE, (target, out) -> {
			Object value = target.readTargetProperty(out);
			session.setAttribute(out.name, value);
		});
	}
}