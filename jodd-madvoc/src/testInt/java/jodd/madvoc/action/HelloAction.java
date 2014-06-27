// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.ScopeType;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;
import jodd.mutable.MutableInteger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@MadvocAction
public class HelloAction {

	// ----------------------------------------------------------------

	@Action
	public void view() {
	}

	// ----------------------------------------------------------------

	@InOut
	private String name;
	public void setName(String name) {
		this.name = "planet " + name;
	}

	@In
	MutableInteger data;

	@Out
	String retv;

	/**
	 * Action mapped to '/hello.world.html'
	 * Result mapped to '/hello.world.ok.jsp'
	 */
	@Action
	public String world() {
		retv = "and Universe " + data;
		return "ok";
	}

	@Action
	public String planet() {
		return null;
	}

	// ----------------------------------------------------------------

	@InOut("p")
	Person person;      // Due to create == true, person will be instanced on first access.

	/**
	 * Action mapped to '/hello.bean.html'
	 * Result mapped to '/hello.bean.jsp' and aliased in {@link MySimpleConfigurator} to /hi-bean.jsp
	 */
	@Action
	public void bean() {
	}

	// ----------------------------------------------------------------

	@In(scope = ScopeType.SERVLET)
	HttpServletResponse servletResponse;

	/**
	 * Action mapped to '/hello.again.html'
	 * No result.
	 */
	@Action
	public String direct() throws IOException {
		servletResponse.getWriter().print("Direct stream output");
		return "none:";
	}

	@In(scope = ScopeType.SERVLET)
	Map<String, String> requestParamMap;

	@In(scope = ScopeType.SERVLET)
	@Out
	String requestMethod;

	static class ReqReqOut {
		@Out
		public String name;
	}

	@In(scope = ScopeType.SERVLET)
	@Out
	String requestBody;

	@Action
	public void reqreq(ReqReqOut reqReqOut) {
		String hey = requestParamMap.get("hey");

		reqReqOut.name = hey;
	}

	// ----------------------------------------------------------------

	// since 'hello.jsp' exist, we need to change the class-related
	// part of action prefix
	@Action("/nohello.${:method}")
	public void nojsp() {
	}

	// ----------------------------------------------------------------

	@In @Out
	int chain;

	@Action
	public String chain() {
		chain++;
		return "chain:/hello.link.html";
	}

	@Action
	public void link() {
		chain++;
	}

	// ----------------------------------------------------------------

	@In("ppp")
	List<Person> plist;

	@In("ppp")
	Person[] parray;

	@In("ppp")
	LinkedHashMap<String, Person> pmap;

	@Out
	String result;

	@Action
	public String many() {
		System.out.println("default action name");
		StringBuilder sb = new StringBuilder();

		if (plist == null) {
			sb.append('-');
		} else {
			for (int i = 0; i < plist.size(); i++) {
				sb.append(i).append(' ').append(plist.get(i).getName()).append('-').append(plist.get(i).getData());
				sb.append('\n');
			}
		}

		if (parray == null) {
			sb.append('-');
		} else {
			for (int i = 0; i < parray.length; i++) {
				sb.append(i).append(' ').append(parray[i].getName()).append('-').append(parray[i].getData());
				sb.append('\n');
			}
		}

		if (pmap == null) {
			sb.append('-');
		} else {
			sb.append(pmap);
		}

		result = sb.toString();

		return "ok";
	}

	// ----------------------------------------------------------------

	@Action
	public String backback() {
		return "###default.big";
	}

}