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

package jodd.joy.vtor;

import jodd.bean.BeanTemplateParser;
import jodd.joy.i18n.LocalizationUtil;
import jodd.util.StringPool;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.Violation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class VtorUtil {

	/**
	 * Returns JSON violations string. Contains javascript array with elements that contain:
	 * <ul>
	 * <li>name - violation name</li>
	 * <li>msg - message code i.e. constraint class name</li>
	 * </ul>
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
	public static String resolveValidationMessage(HttpServletRequest request, Violation violation) {
		ValidationConstraint vc = violation.getConstraint();
		String key = vc != null ? vc.getClass().getName() : violation.getName();
		String msg = LocalizationUtil.findMessage(request, key);
		if (msg != null) {
			return beanTemplateParser.parse(msg, violation);
		}
		return null;
	}

	private static BeanTemplateParser beanTemplateParser = new BeanTemplateParser();

}
