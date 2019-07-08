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

import jodd.madvoc.macro.PathMacros;
import jodd.util.ArraysUtil;

/**
 * Single path chunk.
 */
public class RouteChunk {

	private final String value;
	private RouteChunk[] children;
	private final PathMacros pathMacros;
	private final Routes routes;
	private final RouteChunk parent;
	private final boolean hasMacros;
	private ActionRuntime actionRuntime;

	protected RouteChunk(final Routes routes, final RouteChunk parent, final String value) {
		this.routes = routes;
		this.parent = parent;
		this.value = value;
		this.pathMacros = routes.buildActionPathMacros(value);
		if (pathMacros != null) {
			this.hasMacros = true;
		}
		else {
			this.hasMacros = parent != null && parent.hasMacros;
		}
	}

	// ---------------------------------------------------------------- register

	/**
	 * Adds a new child to the tree.
	 */
	public RouteChunk add(final String newValue) {
		RouteChunk routeChunk = new RouteChunk(routes, this, newValue);
		if (children == null) {
			children = new RouteChunk[] {routeChunk};
		}
		else {
			children = ArraysUtil.append(children, routeChunk);
		}
		return routeChunk;
	}

	/**
	 * Finds existing chunk or creates a new one if does not exist.
	 */
	public RouteChunk findOrCreateChild(final String value) {
		if (children != null) {
			for (RouteChunk child : children) {
				if (child.get().equals(value)) {
					return child;
				}
			}
		}
		return add(value);
	}

	/**
	 * Binds chunk to an action runtime.
	 */
	public void bind(final ActionRuntime actionRuntime) {
		this.actionRuntime = actionRuntime;
		this.actionRuntime.bind(this);
	}

	/**
	 * Returns {@code true} if this chunk is an endpoint and therefore bound to
	 * the configuration.
	 */
	public boolean isEndpoint() {
		return actionRuntime != null;
	}

	/**
	 * Returns associated action runtime with this chunk.
	 * May be {@code null} if this chunk is not an endpoint.
	 */
	public ActionRuntime value() {
		return actionRuntime;
	}

	// ---------------------------------------------------------------- get/set

	/**
	 * Returns chunks string value.
	 */
	public String get() {
		return value;
	}

	/**
	 * Returns parent chunk unless it is a root.
	 */
	public RouteChunk parent() {
		return parent;
	}

	/**
	 * Returns path macros if this chunk has it, otherwise returns {@code null}.
	 */
	public PathMacros pathMacros() {
		return pathMacros;
	}

	public boolean hasMacrosOnPath() {
		return hasMacros;
	}

	/**
	 * Returns all the children or {@code null} if no children exist.
	 */
	public RouteChunk[] children() {
		return children;
	}

	// ---------------------------------------------------------------- lookup/match

	/**
	 * Returns {@code true} if path chunk value matches the input.
	 */
	public boolean match(final String value) {
		if (pathMacros == null) {
			return this.value.equals(value);
		}
		return pathMacros.match(value) != -1;
	}

	@Override
	public String toString() {
		return "RouteChunk{" +
			"value='" + value + '\'' +
			'}';
	}
}
