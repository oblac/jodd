// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.ScopeType;
import jodd.madvoc.interceptor.Preparable;
import jodd.madvoc.interceptor.IdRequestInjectorInterceptor;
import jodd.madvoc.interceptor.PrepareInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;
import jodd.madvoc.interceptor.EchoInterceptor;

import java.util.Map;

import madvoc.girl.Girl;

@MadvocAction
public class MiscAction implements Preparable {

	@In(scope = ScopeType.SERVLET)
	Map<String, Object> sessionMap;

	@In(value="requestMap", scope = ScopeType.SERVLET)
	Map<String, Object> rmap;

	@In(scope = ScopeType.SERVLET)
	Map<String, Object> contextMap;


	@Action
	public void view() {
		System.out.println("MiscAction.view");
		System.out.println("sessionMap = " + sessionMap);
		System.out.println("contextMap = " + contextMap);
		System.out.println("rmap = " + rmap);

		sessionMap.put("s", Integer.valueOf(100));
		contextMap.put("c", Integer.valueOf(101));
		rmap.put("r", Integer.valueOf(102));
	}


	public void prepare() {
		System.out.println("MiscAction.prepare");
		if (girl != null) {
			girl.setName("database name");
		}
		System.out.println(girl);
	}


	@In
	Girl girl;

	@Action
	@InterceptedBy({EchoInterceptor.class, IdRequestInjectorInterceptor.class, PrepareInterceptor.class, ServletConfigInterceptor.class})
	public void post() {
		System.out.println("MiscAction.post");
		System.out.println(girl);
	}

	// ----------------------------------------------------------------

	@In(scope = ScopeType.SERVLET)
	int contextMajorVersion;

	@In(scope = ScopeType.SERVLET)
	String requestRequestURI;

	@In(scope = ScopeType.SERVLET)
	String requestQueryString;

	@In(scope = ScopeType.SERVLET)
	String requestLocalAddr;

	@In(scope = ScopeType.SERVLET)
	String requestRemoteAddr;

	@Action
	public void raw() {
		System.out.println("major version: " + contextMajorVersion);
		System.out.println("requestRequestURI = " + requestRequestURI);
		System.out.println("requestQueryString = " + requestQueryString);
		System.out.println("requestLocalAddr = " + requestLocalAddr);
		System.out.println("requestRemoteAddr = " + requestRemoteAddr);
	}
}
