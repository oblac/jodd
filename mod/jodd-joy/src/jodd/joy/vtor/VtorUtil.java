// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.vtor;

import jodd.bean.BeanTemplate;
import jodd.joy.i18n.LocalizationUtil;
import jodd.util.StringPool;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.Violation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class VtorUtil {

	/**
	 * Returns JSON violations string. Contains javascript array with elements that contain:
	 * <li>name - violation name
	 * <li>msg - message code i.e. constraint class name
	 */
	public static String createViolationsJsonString(HttpServletRequest request, List<Violation> violations) {
		if (violations == null) {
			return StringPool.EMPTY;
		}
		StringBuilder sb = new StringBuilder().append('[');
		for (int i = 0, violationsSize = violations.size(); i < violationsSize; i++) {
			Violation violation = violations.get(i);
			if (i != 0) {
				sb.append(',');
			}
			sb.append('{');
			sb.append("\"name\":\"").append(violation.getName()).append('"').append(',');
			sb.append("\"msg\":\"").append(resolveValidationMessage(request, violation)).append('"');
			sb.append('}');
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * Prepares validation messages.
	 * Key is either validation constraint class name or violation name.
	 */
	protected static String resolveValidationMessage(HttpServletRequest request, Violation violation) {
		ValidationConstraint vc = violation.getConstraint();
		String key = vc != null ? vc.getClass().getName() : violation.getName();
		String msg = LocalizationUtil.findMessage(request, key);
		if (msg != null) {
			return BeanTemplate.parse(msg, violation);
		}
		return null;
	}

}
