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

import jodd.methref.Methref;

/**
 * Result - cool, neat helper for results that are dealing with the path.
 * It allows to specify any action in an easy way.
 */
public class Result {

	protected Class<? extends ActionResult> actionResult;
	protected String resultValue;
	protected Methref methref;
	protected Class target;

	// ---------------------------------------------------------------- core

	/**
	 * Specifies generic result type for rendering.
	 */
	public Result use(Class<? extends ActionResult> actionResult) {
		this.actionResult = actionResult;
		return this;
	}

	/**
	 * Defines raw result value to be rendered.
	 */
	public Result value(String resultValue) {
		this.resultValue = resultValue;
		return this;
	}

	/**
	 * Returns either result value or action method reference.
	 */
	public String value() {
		if (methref != null) {
			String methodName = methref.ref();
			return target.getName() + "#" + methodName;
		}
		return resultValue;
	}

	/**
	 * Wraps action class and returns <code>MethRef</code> object
	 * (proxified target) so user can choose the method.
	 */
	protected <T> T wrapAction(Class<T> target) {
		this.target = target;
		this.methref = Methref.on(target);
		this.resultValue = null;
		return (T) methref.to();
	}

	// ---------------------------------------------------------------- forward

	/**
	 * Basic forwarding to default result.
	 */
	public void forward() {
		actionResult = ServletDispatcherResult.class;
		resultValue = null;
		methref = null;
	}

	/**
	 * Basic forwarding with provided result.
	 */
	public void forwardTo(String path) {
		actionResult = ServletDispatcherResult.class;
		resultValue = path;
		methref = null;
	}

	/**
	 * Forward to action method of provided action.
	 */
	public <T> T forwardTo(Class<T> target) {
		actionResult = ServletDispatcherResult.class;
		return wrapAction(target);
	}

	/**
	 * Forward to action method of current action class.
	 */
	public <T> T forwardTo(T target) {
		actionResult = ServletDispatcherResult.class;
		return (T) wrapAction(target.getClass());
	}

	/**
	 * Appends forward by adding method result value.
	 */
	public void forwardTo(Result result, String append) {
		resultValue = "/<" + result.value() + ">.." + append;
		methref = null;
	}

	// ---------------------------------------------------------------- redirect

	/**
	 * Basic redirection to path.
	 */
	public void redirectTo(String path) {
		actionResult = ServletRedirectResult.class;
		resultValue = path;
		methref = null;
	}

	/**
	 * Redirect to specified path.
	 */
	public <T> T redirectTo(Class<T> target) {
		actionResult = ServletRedirectResult.class;
		return wrapAction(target);
	}

	/**
	 * Redirect to method of this class.
	 */
	public <T> T redirectTo(T target) {
		actionResult = ServletRedirectResult.class;
		return (T) wrapAction(target.getClass());
	}

	/**
	 * Appends redirection definition.
	 */
	public void redirectTo(Result result, String append) {
		resultValue = "/<" + result.value() + ">" + append;
		methref = null;
	}

	/**
	 * Permanent redirection to given path.
	 */
	public void urlTo(String path) {
		actionResult = ServletUrlRedirectResult.class;
		resultValue = path;
		methref = null;
	}

	// ---------------------------------------------------------------- chain

	/**
	 * Basic chain to path.
	 */
	public void chainTo(String path) {
		actionResult = ChainResult.class;
		resultValue = path;
		methref = null;
	}

	/**
	 * Chains to specified path.
	 */
	public <T> T chainTo(Class<T> target) {
		actionResult = ChainResult.class;
		return wrapAction(target);
	}

	/**
	 * Chains to method of this class.
	 */
	public <T> T chainTo(T target) {
		actionResult = ChainResult.class;
		return (T) wrapAction(target.getClass());
	}

	// ---------------------------------------------------------------- move

	/**
	 * Basic move to path.
	 */
	public void moveTo(String path) {
		actionResult = MoveResult.class;
		resultValue = path;
		methref = null;
	}

	/**
	 * Moves to specified path.
	 */
	public <T> T moveTo(Class<T> target) {
		actionResult = MoveResult.class;
		return wrapAction(target);
	}

	/**
	 * Moves to method of this class.
	 */
	public <T> T moveTo(T target) {
		actionResult = MoveResult.class;
		return (T) wrapAction(target.getClass());
	}

	// ---------------------------------------------------------------- other results

	/**
	 * Do nothing.
	 * @see jodd.madvoc.result.NoneResult
	 */
	public void nothing() {
		actionResult = NoneResult.class;
		resultValue = null;
	}

	/**
	 * Returns text.
	 * @see jodd.madvoc.result.TextResult
	 */
	public void text(String text) {
		actionResult = TextResult.class;
		resultValue = text;
	}

	/**
	 * Redirects to url.
	 * @see jodd.madvoc.result.ServletUrlRedirectResult
	 */
	public void url(String url) {
		actionResult = ServletUrlRedirectResult.class;
		resultValue = url;
	}

	// ---------------------------------------------------------------- direct

	/**
	 * Returns action result type.
	 */
	public Class<? extends ActionResult> getActionResult() {
		return actionResult;
	}

	/**
	 * Returns action result value.
	 */
	public Object getResultValue() {
		return resultValue;
	}

}