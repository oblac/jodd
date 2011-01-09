// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.madvoc.interceptor;

import jodd.joy.i18n.I18nInterceptor;
import jodd.madvoc.interceptor.ActionInterceptorStack;
import jodd.madvoc.interceptor.ServletConfigInterceptor;

/**
 * Public interceptor stack for pages that are available to everyone.
 */
public class PublicInterceptorStack extends ActionInterceptorStack {

	public PublicInterceptorStack() {
		super(
				I18nInterceptor.class,
				ServletConfigInterceptor.class);
	}
}
