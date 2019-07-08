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

import jodd.madvoc.component.ScopeDataInspector;

/**
 * Simple data object that holds IN and OUT injection points for a target,
 * for all scopes.
 * It also holds the {@link ScopeDataInspector} instance, as it may
 * be handy later. It's not super nice, but it works.
 */
public class ScopeData {

	private final ScopeDataInspector scopeDataInspector;
	private final InjectionPoint in[];
	private final InjectionPoint out[];

	public ScopeData(
			final ScopeDataInspector scopeDataInspector,
			final InjectionPoint[] allIns,
			final InjectionPoint[] allOuts) {
		this.scopeDataInspector = scopeDataInspector;
		this.in = allIns;
		this.out = allOuts;
	}

	public InjectionPoint[] in() { return in;}

	public InjectionPoint[] out() { return out;}

	public ScopeDataInspector inspector() {
		return scopeDataInspector;
	}

}
