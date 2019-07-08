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

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;
import jodd.util.StringUtil;

/**
 * Simple interceptor that measures time and prints out information about invoked actions.
 * User may inherit it and change the way message is printed.
 */
public class EchoInterceptor implements ActionInterceptor {

	protected String prefixIn = "-----> ";
	protected String prefixOut = "<----- ";

	public void setPrefixIn(final String prefixIn) {
		this.prefixIn = prefixIn;
	}

	public void setPrefixOut(final String prefixOut) {
		this.prefixOut = prefixOut;
	}

	// ---------------------------------------------------------------- code

	/**
	 * Measure action invocation time.
	 */
	@Override
	public Object intercept(final ActionRequest actionRequest) throws Exception {
		printBefore(actionRequest);
		long startTime = System.currentTimeMillis();
		Object result = null;
		try {
			result = actionRequest.invoke();
		} catch (Exception ex) {
			result = "<exception>";
			throw ex;
		} catch (Throwable th) {
			result = "<throwable>";
			throw new Exception(th);
		} finally {
			long executionTime = System.currentTimeMillis() - startTime;
			printAfter(actionRequest, executionTime, result);
		}
		return result;
	}

	/**
	 * Prints out the message. User can override this method and modify the way
	 * the message is printed.
	 */
	protected void printBefore(final ActionRequest request) {
		StringBuilder message = new StringBuilder(prefixIn);

		message.append(request.getActionPath()).append("   [").append(request.getActionRuntime().createActionString()).append(']');
		out(message.toString());
	}

	/**
	 * Prints out the message. User can override this method and modify the way
	 * the message is printed.
	 */
	protected void printAfter(final ActionRequest request, final long executionTime, final Object result) {
		StringBuilder message = new StringBuilder(prefixOut);

		String resultString = StringUtil.toSafeString(result);
		if (resultString.length() > 70) {
			resultString = resultString.substring(0, 70);
			resultString += "...";
		}
		message.append(request.getActionPath()).append("  (")
				.append(resultString).append(") in ").append(executionTime)
				.append("ms.");
		out(message.toString());
	}

	/**
	 * Outputs info message. By default, it outputs it to console.
	 */
	protected void out(final String message) {
		System.out.println(message);
	}

}
