// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.madvoc.interceptor;

import jodd.joy.i18n.I18nInterceptor;
import jodd.madvoc.interceptor.ActionInterceptorStack;
import jodd.madvoc.interceptor.ServletConfigInterceptor;

/**
 * Default interceptor stack.
 */
public class DefaultInterceptorStack extends ActionInterceptorStack {

	public DefaultInterceptorStack() {
		super(
				I18nInterceptor.class,
				ServletConfigInterceptor.class);
	}
}
