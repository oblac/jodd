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

import java.util.function.Consumer;

/**
 * Result - cool, neat helper for results that are dealing with the path.
 * It allows to specify any action in an easy way.
 */
public class Result {

	protected final Class<? extends ActionResult> actionResult;
	protected Object resultValue;
	protected Methref methref;
	protected Class target;

	protected Result(Class<? extends ActionResult> actionResult) {
		this.actionResult = actionResult;
	}

	public static ValueResult of(Class<? extends ActionResult> actionResult) {
		return new ValueResult(actionResult);
	}

	public static class StageResult extends Result {

		protected StageResult(Class<? extends ActionResult> actionResult) {
			super(actionResult);
			resultValue = null;
			methref = null;
		}

		/**
		 * Basic redirection to path.
		 */
		public Result to(String path) {
			resultValue = path;
			methref = null;
			return this;
		}

		/**
		 * Redirect to specified path.
		 */
		public <T> Result to(Class<T> target, Consumer<T> consumer) {
			consumer.accept(wrapAction(target));
			return this;
		}

		@SuppressWarnings("unchecked")
		public <T> Result to(T target, Consumer<T> consumer) {
			return to((Class<T>)target.getClass(), consumer);
		}
	}

	public static class ValueResult<T> extends Result {

		protected ValueResult(Class<? extends ActionResult> actionResult) {
			super(actionResult);
			resultValue = null;
			methref = null;
		}

		public Result with(T t) {
			this.resultValue = t;
			return this;
		}
	}


	// ---------------------------------------------------------------- forward

	/**
	 * Basic forwarding to default result.
	 */
	public static ForwardResult forward() {
		return new ForwardResult();
	}

	public static class ForwardResult extends StageResult {

		protected ForwardResult() {
			super(ServletDispatcherResult.class);
			resultValue = null;
			methref = null;
		}

		/**
		 * Appends forward by adding method result value.
		 */
		public Result to(Result result, String append) {
			resultValue = "/<" + result.value() + ">.." + append;
			methref = null;
			return this;
		}
	}


	// ---------------------------------------------------------------- redirect

	public static RedirectResult redirect() {
		return new RedirectResult();
	}

	public static class RedirectResult extends StageResult {

		protected RedirectResult() {
			super(ServletRedirectResult.class);
			resultValue = null;
			methref = null;
		}

		/**
		 * Appends redirection definition.
		 */
		public Result to(Result result, String append) {
			resultValue = "/<" + result.value() + ">" + append;
			methref = null;
			return this;
		}
	}


	// ---------------------------------------------------------------- chain & move

	public static StageResult chain() {
		return new StageResult(ChainResult.class);
	}

	public static StageResult move() {
		return new StageResult(MoveResult.class);
	}


	// ---------------------------------------------------------------- value

	public static ValueResult<String> url() {
		return new ValueResult<>(ServletUrlRedirectResult.class);
	}

	public static ValueResult<?> none() {
		return new ValueResult<>(NoneResult.class);
	}

	public static ValueResult<String> text() {
		return new ValueResult<>(TextResult.class);
	}

	public static ValueResult<Object> json() {
		return new ValueResult<>(JSONActionResult.class);
	}

	// ---------------------------------------------------------------- direct

	/**
	 * Returns action result type.
	 */
	public Class<? extends ActionResult> actionResult() {
		return actionResult;
	}

	/**
	 * Returns action result value.
	 */
	public Object resultValue() {
		return resultValue;
	}

	/**
	 * Returns either result value or action method reference.
	 */
	public Object value() {
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



}