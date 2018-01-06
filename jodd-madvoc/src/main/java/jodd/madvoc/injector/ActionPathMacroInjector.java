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
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.config.RouteChunk;
import jodd.madvoc.macro.PathMacros;
import jodd.util.StringUtil;

/**
 * Injects macro values from action path into the action bean.
 * Path macros are considered to be in {@link jodd.madvoc.ScopeType#REQUEST request scope}.
 */
public class ActionPathMacroInjector implements Injector {
	private final static ScopeType SCOPE_TYPE = ScopeType.REQUEST;

	@Override
	public void inject(ActionRequest actionRequest) {
		ActionRuntime actionRuntime = actionRequest.actionRuntime();
		RouteChunk routeChunk = actionRuntime.routeChunk();

		if (!routeChunk.hasMacrosOnPath()) {
			// no action path macros at all, just exit
			return;
		}

		final Targets targets = actionRequest.targets();
		if (!targets.usesScope(SCOPE_TYPE)) {
			return;
		}

		// inject
		final String[] actionPath = actionRequest.actionPathChunks();

		int ndx = actionPath.length - 1;

		RouteChunk chunk = routeChunk;
		while (chunk.parent() != null) {
			PathMacros pathMacros = chunk.pathMacros();

			if (pathMacros != null) {
				injectMacros(actionPath[ndx], pathMacros, targets);
			}
			ndx--;
			chunk = chunk.parent();
		}
	}

	private void injectMacros(String actionPath, PathMacros pathMacros, final Targets targets) {
		String[] names = pathMacros.names();
		String[] values = pathMacros.extract(actionPath);

		for (int ndx = 0; ndx < values.length; ndx++) {
			String value = values[ndx];

			if (StringUtil.isEmpty(value)) {
				continue;
			}

			String macroName = names[ndx];

			targets.forEachTargetAndInScopes(SCOPE_TYPE, (target, in) -> {
				String name = in.matchedPropertyName(macroName);

				if (name != null) {
					target.writeValue(name, value, true);
				}
			});
		}
	}
}
