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

package jodd.madvoc.result;

import jodd.bean.BeanUtil;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionDef;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.WebApplication;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.component.ResultMapper;
import jodd.util.ClassUtil;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletDispatcherResultTest {

	@Test
	public void testServletDispatcherLookup() throws Exception {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();

		final List<String> targets = new ArrayList<>();

		ServletDispatcherResult sdr = new ServletDispatcherResult() {
			@Override
			protected boolean targetExists(ActionRequest actionRequest, String target) {
				targets.add(target);
				return false;
			}
		};

		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		BeanUtil.declared.setProperty(sdr, "resultMapper", resultMapper);

		ActionRequest actionRequest = createActionRequest("/hello.world.html");
		sdr.render(actionRequest, "ok");

		assertEquals("[" +
				"/hello.world.html.ok.jspf, " +
				"/hello.world.html.ok.jsp, " +
				"/hello.world.html.jspf, " +
				"/hello.world.html.jsp, " +
				"/hello.world.ok.jspf, " +
				"/hello.world.ok.jsp, " +
				"/hello.world.jspf, " +
				"/hello.world.jsp, " +
				"/hello.ok.jspf, " +
				"/hello.ok.jsp, " +
				"/hello.jspf, " +
				"/hello.jsp, " +
				"/ok.jspf, " +
				"/ok.jsp" +
				"]", targets.toString());


		// folder

		targets.clear();

		actionRequest = createActionRequest("/pak/hello.world.html");
		sdr.render(actionRequest, "ok");

		assertEquals("[" +
				"/pak/hello.world.html.ok.jspf, " +
				"/pak/hello.world.html.ok.jsp, " +
				"/pak/hello.world.html.jspf, " +
				"/pak/hello.world.html.jsp, " +
				"/pak/hello.world.ok.jspf, " +
				"/pak/hello.world.ok.jsp, " +
				"/pak/hello.world.jspf, " +
				"/pak/hello.world.jsp, " +
				"/pak/hello.ok.jspf, " +
				"/pak/hello.ok.jsp, " +
				"/pak/hello.jspf, " +
				"/pak/hello.jsp, " +
				"/pak/ok.jspf, " +
				"/pak/ok.jsp" +
				"]", targets.toString());

		// null result

		targets.clear();

		actionRequest = createActionRequest("/hello.world.html");
		sdr.render(actionRequest, null);

		assertEquals("[" +
				"/hello.world.html.jspf, " +
				"/hello.world.html.jsp, " +
				"/hello.world.jspf, " +
				"/hello.world.jsp, " +
				"/hello.jspf, " +
				"/hello.jsp" +
				"]", targets.toString());
	}

	protected ActionRequest createActionRequest(String actionPath) {
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		HttpSession httpSession = mock(HttpSession.class);
		ServletContext servletContext = mock(ServletContext.class);

		when(servletRequest.getSession()).thenReturn(httpSession);
		when(httpSession.getServletContext()).thenReturn(servletContext);

		MadvocController madvocController = new MadvocController();

		Object action = new Object();
		ActionConfig actionConfig = new ActionConfig(
				Action.class,
				ClassUtil.findMethod(Action.class, "view"),
				null, null,
				new ActionDef(actionPath, "GET"),
				null, false, null, null);

		return new ActionRequest(madvocController, actionConfig.getActionPath(), actionConfig, action, servletRequest, servletResponse);
	}

	public class Action {
		public void view() {}
	}

}
