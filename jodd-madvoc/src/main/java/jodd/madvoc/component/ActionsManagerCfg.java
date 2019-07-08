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

package jodd.madvoc.component;

import jodd.madvoc.macro.PathMacros;
import jodd.madvoc.macro.RegExpPathMacros;

import static jodd.util.StringPool.COLON;
import static jodd.util.StringPool.LEFT_BRACE;
import static jodd.util.StringPool.RIGHT_BRACE;

abstract class ActionsManagerCfg {

	protected boolean detectDuplicatePathsEnabled;
	protected Class<? extends PathMacros> pathMacroClass;
	protected String[] pathMacroSeparators;
	protected boolean strictRoutePaths;
	protected String[] pathExtensionsToStrip = new String[]{"htm", "html"};

	public ActionsManagerCfg() {
		this.detectDuplicatePathsEnabled = true;
		this.pathMacroClass = RegExpPathMacros.class; //WildcardPathMacros.class;
		this.pathMacroSeparators = new String[] {LEFT_BRACE, COLON, RIGHT_BRACE};
		this.strictRoutePaths = false;
	}

	/**
	 * Returns current implementation for path macros.
	 */
	public Class<? extends PathMacros> getPathMacroClass() {
		return pathMacroClass;
	}

	/**
	 * Sets implementation for path macros.
	 */
	public void setPathMacroClass(final Class<? extends PathMacros> pathMacroClass) {
		this.pathMacroClass = pathMacroClass;
	}

	public String[] getPathMacroSeparators() {
		return pathMacroSeparators;
	}

	/**
	 * Sets path macro separators.
	 */
	public void setPathMacroSeparators(final String... pathMacroSeparators) {
		this.pathMacroSeparators = pathMacroSeparators;
	}

	public boolean isDetectDuplicatePathsEnabled() {
		return detectDuplicatePathsEnabled;
	}

	public void setDetectDuplicatePathsEnabled(final boolean detectDuplicatePathsEnabled) {
		this.detectDuplicatePathsEnabled = detectDuplicatePathsEnabled;
	}

	public boolean isStrictRoutePaths() {
		return strictRoutePaths;
	}

	/**
	 * Defines if the router should trim parts of the path to match the action path.
	 */
	public void setStrictRoutePaths(final boolean strictRoutePaths) {
		this.strictRoutePaths = strictRoutePaths;
	}

	public String[] getPathExtensionsToStrip() {
		return pathExtensionsToStrip;
	}

	public void setPathExtensionsToStrip(final String... pathExtensionsToStrip) {
		this.pathExtensionsToStrip = pathExtensionsToStrip;
	}
}
