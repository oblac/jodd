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
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.config.RouteChunk;
import jodd.madvoc.config.Targets;
import jodd.madvoc.macro.PathMacros;
import jodd.util.StringUtil;

/**
 * Special scope that is used from the {@link RequestScope}.
 */
public class ActionPathMacroInjector {

	private final MadvocScope madvocScope;

	public ActionPathMacroInjector(final MadvocScope bindedScope) {
		this.madvocScope = bindedScope;
	}

	public void inject(final ActionRequest actionRequest, final Targets targets) {
		final ActionRuntime actionRuntime = actionRequest.getActionRuntime();
		final RouteChunk routeChunk = actionRuntime.getRouteChunk();

		if (!routeChunk.hasMacrosOnPath()) {
			// no action path macros at all, just exit
			return;
		}

		// inject
		final String[] actionPath = actionRequest.getActionPathChunks();

		int ndx = actionPath.length - 1;

		RouteChunk chunk = routeChunk;
		while (chunk.parent() != null) {
			final PathMacros pathMacros = chunk.pathMacros();

			if (pathMacros != null) {
				injectMacros(actionPath[ndx], pathMacros, targets);
			}
			ndx--;
			chunk = chunk.parent();
		}

	}

	private void injectMacros(final String actionPath, final PathMacros pathMacros, final Targets targets) {
		final String[] names = pathMacros.names();
		final String[] values = pathMacros.extract(actionPath);

		for (int ndx = 0; ndx < values.length; ndx++) {
			final String value = values[ndx];

			if (StringUtil.isEmpty(value)) {
				continue;
			}

			final String macroName = names[ndx];

			targets.forEachTargetAndIn(madvocScope, (target, in) -> {
				final String name = in.matchedName(macroName);

				if (name != null) {
					target.writeValue(name, value, true);
				}
			});
		}
	}

}
