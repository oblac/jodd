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

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;
import jodd.madvoc.meta.scope.Body;
import jodd.madvoc.result.Chain;
import jodd.madvoc.result.NoResult;
import jodd.mutable.MutableInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

@MadvocAction
public class HelloAction {

	// ----------------------------------------------------------------

	@Action
	public void view() {
	}

	// ----------------------------------------------------------------

	@In @Out
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

	@In("p") @Out("p")
	Person person;      // Due to create == true, person will be instanced on first access.

	@Action
	public void bean() {
	}

	// ----------------------------------------------------------------

	@In
	HttpServletResponse servletResponse;

	/**
	 * Action mapped to '/hello.again.html'
	 * No result.
	 */
	@Action
	public NoResult direct() throws IOException {
		servletResponse.getWriter().print("Direct stream output");
		return NoResult.value();
	}

	@Out
	public String getReqMethod() {
		return servletRequest.getMethod();
	}

	static class ReqReqOut {
		@Out
		public String name;
	}

	@In
	HttpServletRequest servletRequest;

	@In @Body
	String requestBody;

	@Out
	public String getReqBody() {
		return requestBody;
	}

	@Action
	public void reqreq(ReqReqOut reqReqOut) {
		String hey = servletRequest.getParameter("hey");

		reqReqOut.name = hey;
	}

	// ----------------------------------------------------------------

	// since 'hello.jsp' exist, we need to change the class-related
	// part of action prefix
	@Action("/nohello.{:name}")
	public void nojsp() {
	}

	// ----------------------------------------------------------------

	@In @Out
	int chain;

	@Action
	public Chain chain() {
		chain++;
		return Chain.to("/hello.link.html");
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