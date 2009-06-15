// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.ScopeType;
import jodd.madvoc.interceptor.EchoInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;
import jodd.mutable.MutableInteger;
import jodd.petite.meta.PetiteInject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import madvoc.biz.FooService;

/**
 * Default usage.
 */
@MadvocAction
@InterceptedBy(MyInterceptorStack.class)
public class HelloAction {

	// ---------------------------------------------------------------- 1

	@In
	private String name;
	public void setName(String name) {
		this.name = name + "xxx";
	}

	@In
	MutableInteger data;

	@Out
	String retv;

	@PetiteInject
	FooService fooService;      // example of using petite container


	/**
	 * Action mapped to '/hello.world.html'
	 * Result mapped to '/hello.world.ok.jsp'
	 */
	@Action
	public String world() {
		System.out.println(">HelloAction.world " + name + ' ' + data);
		retv = " and Universe";
		fooService.hello();
		return "ok";
	}

	// ---------------------------------------------------------------- 2

	@In("p")
	Person person;      // Due to create == true, person will be instanced on first access.

	/**
	 * Action mapped to '/hello.all.html'
	 * Result mapped to '/hello.all.jsp' and aliased in {@link MySimpleConfigurator} to /hi-all.jsp
	 */
	@Action
	public void all() {
		System.out.println(">HelloAction.all " + person);
	}

	// ---------------------------------------------------------------- 3

	@In("ppp")
	List<Person> plist;

	@In("ppp")
	Person[] parray;

	@In("ppp")
	Map<String, Person> pmap;

	@In(scope=ScopeType.CONTEXT)
	HttpServletResponse servletResponse;

	/**
	 * Action mapped to '/hello.again.html'
	 * No result.
	 */
	@Action
	public String again() throws IOException {
		System.out.println(">HelloAction.again");

		if (plist == null) {
			System.out.println("-");
		} else {
			for (int i = 0; i < plist.size(); i++) {
				System.out.println(i + " " + plist.get(i));
			}
		}

		if (parray == null) {
			System.out.println("-");
		} else {
			for (int i = 0; i < parray.length; i++) {
				System.out.println(i + " " + parray[i]);
			}
		}

		if (pmap == null) {
			System.out.println("-");
		} else {
			System.out.println(pmap);
		}

		servletResponse.getWriter().print("Direct stream output...");
		return "none:";
	}

	// ---------------------------------------------------------------- 4

	/**
	 * Forward.
	 */
	@Action
	public String fff() {
		System.out.println(">HelloAction.fff");
		return "forward:/hello.again.html";
	}

	// ---------------------------------------------------------------- 5

	@Action
	public String bigchange() {
		return "##default.ok";
	}

	@Action
	public void noresult() {
	}

	@Action
	public String chain() {
		System.out.println("HelloAction.chain");
		return "chain:/hello.link.html";
	}

	@Action
	public void link() {
		System.out.println("HelloAction.link");
	}


	// ---------------------------------------------------------------- 6

	@InterceptedBy({EchoInterceptor.class, ServletConfigInterceptor.class})
	@Action
	public void defint1() {

	}
	@InterceptedBy({EchoInterceptor.class, ServletConfigAltInterceptor.class})
	@Action
	public String defint2() {
		return "#defint1";
	}
}
