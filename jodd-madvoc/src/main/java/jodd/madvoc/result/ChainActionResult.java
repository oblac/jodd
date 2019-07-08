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
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.scope.MadvocContext;
import jodd.util.StringPool;

/**
 * Process chain results. Chaining is very similar to forwarding, except it is done
 * by {@link jodd.madvoc.MadvocServletFilter} and not by container. Chaining to next action request
 * happens after the complete execution of current one: after all interceptors and this result has been
 * finished.
 */
public class ChainActionResult implements ActionResult {

	@In @MadvocContext
	protected ResultMapper resultMapper;

	/**
	 * Sets the {@link jodd.madvoc.ActionRequest#setNextActionPath(String) next action request} for the chain.
	 */
	@Override
	public void render(final ActionRequest actionRequest, final Object resultValue) {
		final Chain chainResult;

		if (resultValue == null) {
			chainResult = Chain.to(StringPool.EMPTY);
		} else {
			if (resultValue instanceof String) {
				chainResult = Chain.to((String)resultValue);
			}
			else {
				chainResult = (Chain) resultValue;
			}
		}

		final String resultBasePath = actionRequest.getActionRuntime().getResultBasePath();

		final String resultPath = resultMapper.resolveResultPathString(resultBasePath, chainResult.path());

		actionRequest.setNextActionPath(resultPath);
	}

}