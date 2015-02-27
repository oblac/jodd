// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

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