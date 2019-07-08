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

package jodd.madvoc.config;

import jodd.madvoc.MadvocException;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.macro.PathMacros;
import jodd.util.ClassUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Utility that deals with routes and route chunks.
 */
public class Routes {
	private static final String ANY_METHOD = StringPool.STAR;

	private final RouteChunk root;
	private final ActionsManager actionsManager;
	private RouteChunk anyMethodChunk;

	public Routes(final ActionsManager actionsManager) {
		this.root = new RouteChunk(this, null, StringPool.EMPTY);
		this.actionsManager = actionsManager;
	}

	public RouteChunk registerPath(String method, String path) {
		if (method == null) {
			method = ANY_METHOD;
		}
		else {
			method = method.toUpperCase();
		}

		RouteChunk chunk = root.findOrCreateChild(method);

		if (method.equals(ANY_METHOD)) {
			// cache common root chunk
			anyMethodChunk = chunk;
		}

		path = StringUtil.cutSurrounding(path, StringPool.SLASH);

		String[] pathChunks = StringUtil.splitc(path, '/');

		for (String pathChunk : pathChunks) {
			chunk = chunk.findOrCreateChild(pathChunk);
		}

		return chunk;
	}

	public ActionRuntime lookup(final String method, final String[] pathChunks) {
		while (true) {
			final ActionRuntime actionRuntime = _lookup(method, pathChunks);
			if (actionRuntime != null) {
				return actionRuntime;
			}

			if (actionsManager.isStrictRoutePaths()) {
				return null;
			}

			// special case
			final String lastPath = pathChunks[pathChunks.length - 1];
			final int lastNdx = lastPath.lastIndexOf('.');
			if (lastNdx == -1) {
				return null;
			}
			final String pathExtension = lastPath.substring(lastNdx + 1);

			if (StringUtil.equalsOne(pathExtension, actionsManager.getPathExtensionsToStrip()) == -1) {
				return null;
			}

			pathChunks[pathChunks.length - 1] = lastPath.substring(0, lastNdx);
		}
	}

	private ActionRuntime _lookup(String method, final String[] pathChunks) {

		// 1 - match method
		if (method != null) {
			method = method.toUpperCase();
			final RouteChunk methodChunk = root.findOrCreateChild(method);
			final ActionRuntime actionRuntime = lookupFrom(methodChunk, pathChunks);
			if (actionRuntime != null) {
				return actionRuntime;
			}
		}

		// 2 - match all methods
		if (anyMethodChunk != null) {
			final ActionRuntime actionRuntime = lookupFrom(anyMethodChunk, pathChunks);
			if (actionRuntime != null) {
				return actionRuntime;
			}
		}

		// nothing found
		return null;
	}

	private ActionRuntime lookupFrom(final RouteChunk chunk, final String[] path) {
		// matched, scan children
		RouteChunk[] children = chunk.children();

		if (children == null) {
			return null;
		}

		for (RouteChunk child : children) {
			ActionRuntime matched = match(child, path, 0);

			if (matched != null) {
				return matched;
			}
		}

		return null;
	}

	private ActionRuntime match(final RouteChunk chunk, final String[] path, final int ndx) {
		final int maxDeep = path.length - 1;
		if (ndx > maxDeep) {
			// too deep, don't go any further
			return null;
		}

		if (!chunk.match(path[ndx])) {
			// no match, continue
			return null;
		}

		if (ndx == maxDeep) {
			// end of the path
			if (chunk.isEndpoint()) {
				return chunk.value();
			}
			return null;
		}

		// matched, scan children
		final RouteChunk[] children = chunk.children();

		if (children == null) {
			return null;
		}


		ActionRuntime matchedRuntime = null;

		for (final RouteChunk child : children) {
			final ActionRuntime match = match(child, path, ndx + 1);

			if (match != null) {
				if (matchedRuntime == null) {
					matchedRuntime = match;
				}
				else {
					// already matched
					if (matchedRuntime.getRouteChunk().hasMacrosOnPath()) {
						if (!match.getRouteChunk().hasMacrosOnPath()) {
							// previous match IS macro; and current one is NOT
							matchedRuntime = match;
						}
					}
				}
			}
		}

		if (matchedRuntime != null) {
			return matchedRuntime;
		}

		return null;
	}

	// ---------------------------------------------------------------- path macros

	/**
	 * Builds {@link PathMacros action path macros} from given action
	 * path chunks. Returns either <code>null</code>, if
	 * no action path contains no macros, or instance of the <code>PathMacro</code>
	 * implementations.
	 */
	public PathMacros buildActionPathMacros(final String actionPath) {
		if (actionPath.isEmpty()) {
			return null;
		}

		PathMacros pathMacros = createPathMacroInstance();

		if (!pathMacros.init(actionPath, actionsManager.getPathMacroSeparators())) {
			return null;
		}

		return pathMacros;
	}

	/**
	 * Creates new <code>PathMacro</code> instance.
	 */
	private PathMacros createPathMacroInstance() {
		try {
			return ClassUtil.newInstance(actionsManager.getPathMacroClass());
		} catch (Exception ex) {
			throw new MadvocException(ex);
		}
	}

}
