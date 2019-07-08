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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Servlet context scope.
 */
public class ServletContextScope implements MadvocScope {

	protected final InstancesInjector instancesInjector = new InstancesInjector(this);

	@Override
	public void inject(final ActionRequest actionRequest, final Targets targets) {
		final HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		inject(servletRequest.getServletContext(), targets);
	}

	@Override
	public void inject(final ServletContext servletContext, final Targets targets) {

		instancesInjector.inject(servletContext, targets);

		final Enumeration<String> attributeNames = servletContext.getAttributeNames();

		while (attributeNames.hasMoreElements()) {
			final String attrName = attributeNames.nextElement();

			targets.forEachTargetAndIn(this, (target, in) -> {
				final String name = in.matchedName(attrName);
				if (name != null) {
					final Object attrValue = servletContext.getAttribute(attrName);
					target.writeValue(name, attrValue, true);
				}
			});
		}

	}

	@Override
	public void inject(final Targets targets) {

	}

	@Override
	public void outject(final ActionRequest actionRequest, final Targets targets) {
		final ServletContext context = actionRequest.getHttpServletRequest().getServletContext();

		targets.forEachTargetAndOut(this, (target, out) -> {
			final Object value = target.readValue(out);
			context.setAttribute(out.name(), value);
		});

	}

}
