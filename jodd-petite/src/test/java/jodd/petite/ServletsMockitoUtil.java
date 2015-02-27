// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Some nice Mockito utils for servlets.
 */
public class ServletsMockitoUtil {

	/**
	 * Creates a session for given session id.
	 */
	public static HttpSession createSession(String sessionId) {
		HttpSession session = mock(HttpSession.class);

		final Map<String, Object> attrs = new HashMap<String, Object>();
		when(session.getId()).thenReturn(sessionId);
		doAnswer(new Answer() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				String key = (String) invocation.getArguments()[0];
				Object value = invocation.getArguments()[1];
				attrs.put(key, value);
			    return null;
			}
		}).when(session).setAttribute(anyString(), anyObject());
		when(session.getAttribute(anyString())).then(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return attrs.get(invocation.getArguments()[0].toString());
			}
		});

		return session;
	}

	public static HttpServletRequest createRequest(HttpSession session) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getSession()).thenReturn(session);
		return request;
	}

	public static ServletRequestEvent createServletRequestEvent(HttpServletRequest request) {
		ServletRequestEvent event = mock(ServletRequestEvent.class);
		when(event.getServletRequest()).thenReturn(request);
		return event;
	}

	public static HttpSessionEvent createHttpSessionEvent(HttpSession session) {
		HttpSessionEvent sessionEvent = mock(HttpSessionEvent.class);
		when(sessionEvent.getSession()).thenReturn(session);
		return sessionEvent;
	}

	public static HttpSessionBindingEvent createHttpSessionBindingEvent(HttpSession session) {
		HttpSessionBindingEvent sessionBindingEvent = mock(HttpSessionBindingEvent.class);
		when(sessionBindingEvent.getSession()).thenReturn(session);
		return sessionBindingEvent;
	}
}