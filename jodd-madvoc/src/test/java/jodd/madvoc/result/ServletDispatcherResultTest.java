// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.bean.BeanUtil;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.WebApplication;
import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.component.ResultsManager;
import jodd.util.ReflectUtil;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletDispatcherResultTest {

	@Test
	public void testServletDispatcherLookup() throws Exception {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();

		final List<String> targets = new ArrayList<String>();

		ServletDispatcherResult sdr = new ServletDispatcherResult() {
			@Override
			protected boolean targetExist(ServletContext servletContext, String target) {
				targets.add(target);
				return false;
			}
		};

		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		BeanUtil.setDeclaredProperty(sdr, "resultMapper", resultMapper);

		ActionRequest actionRequest = createActionRequest();
		sdr.render(actionRequest, "ok");

		assertEquals(
				"[/hello.world.html.ok.jsp, " +
				"/hello.world.html.jsp, " +
				"/hello.world.ok.jsp, " +
				"/hello.world.jsp, " +
				"/hello.ok.jsp, " +
				"/hello.jsp, " +
				"ok.jsp]", targets.toString());
	}

	protected ActionRequest createActionRequest() {
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
				ReflectUtil.findMethod(Action.class, "view"),
				null, null,
				"/hello.world.html", "GET", null);

		return new ActionRequest(madvocController, actionConfig.getActionPath(), actionConfig, action, servletRequest, servletResponse);
	}

	public class Action {
		public void view() {}
	}

}