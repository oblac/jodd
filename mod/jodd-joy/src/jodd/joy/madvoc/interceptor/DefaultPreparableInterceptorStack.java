// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.madvoc.interceptor;

import jodd.joy.i18n.I18nInterceptor;
import jodd.madvoc.interceptor.ActionInterceptorStack;
import jodd.madvoc.interceptor.PrepareAndIdInjectorInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;

/**
 * Preparable version of default interceptor stack.
 */
public class DefaultPreparableInterceptorStack extends ActionInterceptorStack {

	public DefaultPreparableInterceptorStack() {
		super(
				I18nInterceptor.class,
				PrepareAndIdInjectorInterceptor.class,
				ServletConfigInterceptor.class);
	}
}

