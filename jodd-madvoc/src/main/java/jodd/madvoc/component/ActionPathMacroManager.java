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

import jodd.madvoc.MadvocException;
import jodd.madvoc.macro.PathMacros;
import jodd.petite.meta.PetiteInject;

/**
 * Create and manage action path macros.
 */
public class ActionPathMacroManager {

	@PetiteInject
	protected MadvocConfig madvocConfig;

	/**
	 * Builds {@link PathMacros action path macros} from given action
	 * path chunks. Returns either <code>null</code>, if
	 * no action path contains no macros, or instance of the <code>PathMacro</code>
	 * implementations.
	 */
	public PathMacros buildActionPathMacros(String actionPath) {
		PathMacros pathMacros = createPathMacro();

		if (pathMacros.init(actionPath, madvocConfig.getPathMacroSeparators()) == false) {
			return null;
		}

		return pathMacros;
	}

	/**
	 * Creates new <code>PathMacro</code> instance.
	 */
	protected PathMacros createPathMacro() {
		try {
			return madvocConfig.getPathMacroClass().newInstance();
		} catch (Exception ex) {
			throw new MadvocException(ex);
		}
	}

}