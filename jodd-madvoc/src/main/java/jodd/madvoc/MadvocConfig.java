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

package jodd.madvoc;

import jodd.madvoc.macro.PathMacros;
import jodd.madvoc.macro.RegExpPathMacros;
import jodd.util.StringPool;

import java.util.Objects;

import static jodd.util.StringPool.COLON;
import static jodd.util.StringPool.LEFT_BRACE;
import static jodd.util.StringPool.RIGHT_BRACE;

/**
 * Madvoc configuration. This is the single place where component configuration is stored.
 * New custom component that requires configuration may override and enhance this config
 * with new configuration.
 * <p>
 * This class is instantiated in {@link WebApp}.
 */
public final class MadvocConfig {

	@SuppressWarnings({"unchecked"})
	public MadvocConfig() {
		encoding = StringPool.UTF_8;
		pathMacroClass = RegExpPathMacros.class; //WildcardPathMacros.class;
		pathMacroSeparators = new String[] {LEFT_BRACE, COLON, RIGHT_BRACE};
	}

	// ---------------------------------------------------------------- encoding

	private String encoding;

	/**
	 * Returns character encoding.
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * Sets web application character encoding. If set to <code>null</code> encoding will be not applied.
	 */
	public void setEncoding(final String encoding) {
		Objects.requireNonNull(encoding);
		this.encoding = encoding;
	}

	// ---------------------------------------------------------------- path macro class

	private Class<? extends PathMacros> pathMacroClass;
	private String[] pathMacroSeparators;

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

}
