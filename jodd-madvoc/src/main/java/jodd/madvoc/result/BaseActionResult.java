package jodd.madvoc.result;

import jodd.util.ReflectUtil;
import jodd.util.StringPool;

/**
 * Base implementation of {@link jodd.madvoc.result.ActionResult Action result}.
 */
public abstract class BaseActionResult<T> implements ActionResult<T> {

	protected final String resultType;
	protected final Class<T> resultValueType;

	/**
	 * Creates new action result that has a string identification.
	 */
	protected BaseActionResult(String resultType) {
		this.resultType = resultType;
		this.resultValueType = resolveResultValueType();
	}

	/**
	 * Creates new action result without a string identification.
	 */
	protected BaseActionResult() {
		this.resultType = null;
		this.resultValueType = resolveResultValueType();
	}

	/**
	 * Resolves {@link #getResultValueType() result value type} by finding the
	 * first superclass that has this value defined in generics.
	 */
	protected Class<T> resolveResultValueType() {
		Class clazz = this.getClass();

		while (clazz.getSuperclass() != BaseActionResult.class) {
			Class<T> rvt = ReflectUtil.getGenericSupertype(clazz);
			if (rvt != null) {
				return rvt;
			}
			clazz = clazz.getSuperclass();
		}
		return ReflectUtil.getGenericSupertype(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getResultType() {
		return resultType;
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
				(resultType != null ? StringPool.COLON + resultType : StringPool.EMPTY);
	}

}