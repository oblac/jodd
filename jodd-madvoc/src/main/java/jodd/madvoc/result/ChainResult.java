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

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.meta.In;

/**
 * Process chain results. Chaining is very similar to forwarding, except it is done
 * by {@link jodd.madvoc.MadvocServletFilter} and not by container. Chaining to next action request
 * happens after the complete execution of current one: after all interceptors and this result has been
 * finished.
 */
public class ChainResult extends BaseActionResult<String> {

	public static final String NAME = "chain";

	public ChainResult() {
		super(NAME);
	}

	@In(scope = ScopeType.CONTEXT)
	protected ResultMapper resultMapper;

	/**
	 * Sets the {@link jodd.madvoc.ActionRequest#setNextActionPath(String) next action request} for the chain.
	 */
	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		String resultBasePath = actionRequest.getActionConfig().getResultBasePath();

		String resultPath = resultMapper.resolveResultPathString(resultBasePath, resultValue);

		actionRequest.setNextActionPath(resultPath);
	}

}