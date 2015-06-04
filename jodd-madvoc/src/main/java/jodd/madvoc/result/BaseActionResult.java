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

import jodd.util.ReflectUtil;
import jodd.util.StringPool;

/**
 * Base implementation of {@link jodd.madvoc.result.ActionResult Action result}.
 */
public abstract class BaseActionResult<T> implements ActionResult<T> {

	protected final String resultName;
	protected final Class<T> resultValueType;

	/**
	 * Creates new action result that has a string identification.
	 */
	protected BaseActionResult(String resultName) {
		this.resultName = resultName;
		this.resultValueType = resolveResultValueType();
	}

	/**
	 * Creates new action result without a string identification.
	 */
	protected BaseActionResult() {
		this.resultName = null;
		this.resultValueType = resolveResultValueType();
	}

	/**
	 * Resolves {@link #getResultValueType() result value type} by finding the
	 * first superclass that has this value defined in generics.
	 */
	protected Class<T> resolveResultValueType() {
		Class clazz = this.getClass();

		while (clazz.getSuperclass() != BaseActionResult.class) {
			Class<T> rvt = ReflectUtil.getGenericSupertype(clazz, 0);
			if (rvt != null) {
				return rvt;
			}
			clazz = clazz.getSuperclass();
		}
		return ReflectUtil.getGenericSupertype(clazz, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getResultName() {
		return resultName;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<T> getResultValueType() {
		return resultValueType;
	}

	/**
	 * {@inheritDoc}
	 */
	public void init() {
	}

	@Override
	public String toString() {
		return "Result: " + getClass().getSimpleName() +
				(resultName != null ? StringPool.COLON + resultName : StringPool.EMPTY) +
				(resultValueType != null ? StringPool.COLON + resultValueType.getName() : StringPool.EMPTY)
				;
	}

}