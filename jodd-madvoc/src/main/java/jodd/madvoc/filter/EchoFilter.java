package jodd.madvoc.filter;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.interceptor.EchoInterceptor;

/**
 * Filter variant of {@link jodd.madvoc.interceptor.EchoInterceptor}.
 */
public class EchoFilter extends EchoInterceptor implements ActionFilter {

	public Object filter(ActionRequest actionRequest) throws Exception {
		return intercept(actionRequest);
	}
}