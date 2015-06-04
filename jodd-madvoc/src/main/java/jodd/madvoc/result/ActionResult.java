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

/**
 * Action result renders the returned value from an action.
 * Results are singletons for the web application. Results
 * may have a result type, a string identification of the type
 * used when actions return string result.
 */
public interface ActionResult<T> {

	/**
	 * Returns the name of this action result.
	 * Returned name can be <code>null</code> for results
	 * that does not need to be found using string name identification;
	 * i.e. when action does not return a string result.
	 */
	String getResultName();

	/**
	 * Returns type of result value, passed to the {@link #render(jodd.madvoc.ActionRequest, Object) render method}
	 * and defined by generics. Returns <code>null</code> when this action result does not need
	 * to be registered for result value type (eg when used in @Action annotation).
	 */
	Class<T> getResultValueType();

	/**
	 * Renders result on given action result value.
	 * @param actionRequest action request
	 * @param resultValue action method result, may be <code>null</code>
	 */
	void render(ActionRequest actionRequest, T resultValue) throws Exception;

	/**
	 * Initializes the result.
	 */
	void init();

}