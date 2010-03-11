package jodd.joy.madvoc.interceptor;

import jodd.joy.i18n.I18nInterceptor;
import jodd.madvoc.interceptor.ActionInterceptorStack;
import jodd.madvoc.interceptor.PrepareAndIdInjectorInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;

/**
 * Preparable version of public interceptor stack.
 */
public class PublicPreparableInterceptorStack extends ActionInterceptorStack {

	public PublicPreparableInterceptorStack() {
		super(
				I18nInterceptor.class,
				PrepareAndIdInjectorInterceptor.class,
				ServletConfigInterceptor.class);
	}
}

