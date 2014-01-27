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
		this.resultValueType = ReflectUtil.getGenericSupertype(this.getClass());
	}

	/**
	 * Creates new action result without a string identification.
	 */
	protected BaseActionResult() {
		this.resultType = null;
		this.resultValueType = ReflectUtil.getGenericSupertype(this.getClass());
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
		return "result: " + getClass().getSimpleName() +
				(resultType != null ? StringPool.COLON + resultType : StringPool.EMPTY);
	}

}