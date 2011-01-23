// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.petite.meta.PetiteInject;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.MadvocUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Maps action results to result path. Invoked just before the result itself.
 */
public class ResultMapper {

	@PetiteInject
	protected MadvocConfig madvocConfig;

	/**
	 * Returns resolved alias result value or passed on, if alias doesn't exist.
	 */
	protected String resolveAlias(String resultValue) {
		StringBuilder result = new StringBuilder(resultValue.length());
		int i = 0;
		int len = resultValue.length();
		while (i < len) {
			int ndx = resultValue.indexOf('%', i);
			if (ndx == -1) {
				// alias markers not found
				resultValue = (i == 0 ? resultValue : resultValue.substring(i));
				String alias = madvocConfig.lookupPathAlias(resultValue);
				result.append(alias != null ? alias : resultValue);
				break;
			}
			result.append(resultValue.substring(i, ndx));
			ndx++;
			int ndx2 = resultValue.indexOf('%', ndx);
			String alias = (ndx2 == -1 ? resultValue.substring(ndx) : resultValue.substring(ndx, ndx2));

			// process alias
			alias = madvocConfig.lookupPathAlias(alias);
			if (alias != null) {
				result.append(alias);
			}
			i = ndx2 + 1;
		}

		// fix '//' as prefix - it may happens when aliases are used.
		i = 0; len = result.length();
		while (i < len) {
			if (result.charAt(i) != '/') {
				break;
			}
			i++;
		}
		if (i > 1) {
			return result.subSequence(i - 1, len).toString();
		}
		return result.toString();
	}

	/**
	 * Resolves result path from action configuration and result value.
	 * By default, the result value is appended to the class action path and method action path.
	 * If result value starts with path prefix, it represent complete path.
	 * Although result value may be <code>null</code>, result is never <code>null</code>.
	 */
	public String resolveResultPath(ActionConfig cfg, String resultValue) {

		if (resultValue != null) {

			resultValue = resolveAlias(resultValue);

			// absolute paths
			if (resultValue.startsWith(StringPool.SLASH)) {
				return resultValue;
			}
		}

		String resultPath = MadvocUtil.stripHttpMethodFromActionPath(cfg.actionPath);

		// strip extension part
		boolean strip = true;
		if (resultValue != null) {
			if (StringUtil.startsWithChar(resultValue, '.')) {
				resultValue = resultValue.substring(1);
				strip = false;
			}
		}
		if (strip) {
			int dotNdx = MadvocUtil.lastIndexOfDotAfterSlash(resultPath);
			if (dotNdx != -1) {
				resultPath = resultPath.substring(0, dotNdx);
			}
		}

		// method
		boolean addDot = true;
		if (resultValue != null) {
			int i = 0;
			while (i < resultValue.length()) {
				if (resultValue.charAt(i) != '#') {
					break;
				}
				int dotNdx = MadvocUtil.lastIndexOfSlashDot(resultPath);
				if (dotNdx != -1) {
					resultPath = resultPath.substring(0, dotNdx);
					if (resultPath.charAt(dotNdx - 1) == '/') {
						addDot = false;
					}
				}
				i++;
			}
			if (i > 0) {
				resultValue = resultValue.substring(i);
			}
		}

		// finally
		if ((resultValue != null) && (resultValue.length() != 0)) {
			if (addDot) {
				resultPath += StringPool.DOT;	// result separator
			}
			resultPath += resultValue;
		}
		return resultPath;
	}


}
