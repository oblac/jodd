// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.filter;

import jodd.madvoc.interceptor.ActionInterceptor;

/**
 * Action filter is an {@link jodd.madvoc.interceptor.ActionInterceptor interceptor}
 * that will be called before the action invocation and after the rendering phase.
 * This behavior is similar to servlet filters. This is just a marker class.
 */
public interface ActionFilter extends ActionInterceptor {

}