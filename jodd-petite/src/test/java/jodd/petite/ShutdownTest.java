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

package jodd.petite;

import jodd.petite.scope.SessionScope;
import jodd.petite.fixtures.tst.Boo;
import jodd.petite.fixtures.tst.Foo;
import jodd.petite.fixtures.tst.Ses;
import jodd.petite.fixtures.tst.Zoo;
import jodd.servlet.RequestContextListener;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import static jodd.petite.ServletsMockitoUtil.createHttpSessionBindingEvent;
import static jodd.petite.ServletsMockitoUtil.createRequest;
import static jodd.petite.ServletsMockitoUtil.createServletRequestEvent;
import static jodd.petite.ServletsMockitoUtil.createSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ShutdownTest {

	public static final String ATTR_NAME = SessionScope.class.getName() + ".SESSION_BEANS.";

	@Test
	public void testSingletonDestroyMethods() {
		PetiteContainer pc = new PetiteContainer();

		pc.registerPetiteBean(Foo.class, null, null, null, false);
		pc.registerPetiteBean(Zoo.class, null, null, null, false);
		pc.registerPetiteBean(Boo.class, null, null, null, false);

		Boo boo = (Boo) pc.getBean("boo");
		assertEquals(0, boo.getCount2());

		pc.shutdown();

		assertEquals(2, boo.getCount2());
	}

	@Test
	public void testSessionExpired() {
		// http session
		HttpSession session = createSession("S1");
		HttpServletRequest request = createRequest(session);
		ServletRequestEvent requestEvent = createServletRequestEvent(request);
		HttpSessionBindingEvent event = createHttpSessionBindingEvent(session);

		// jodd
		RequestContextListener requestContextListener = new RequestContextListener();

		// start session, init request
		requestContextListener.requestInitialized(requestEvent);

		// petite
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Ses.class, null, null, null, false);

		// callback not yet added
		SessionScope.SessionBeans sessionBeans = (SessionScope.SessionBeans) session.getAttribute(ATTR_NAME);
		assertNull(sessionBeans);

		Ses ses = (Ses) pc.getBean("ses");
		assertNotNull(ses);

		// callback added
		sessionBeans = (SessionScope.SessionBeans) session.getAttribute(ATTR_NAME);
		assertNotNull(sessionBeans);

		ses.setValue("jodd");

		// session expired
		sessionBeans.valueUnbound(event);

		assertEquals("-jodd", ses.getValue());

		pc.shutdown();

		assertEquals("-jodd", ses.getValue());
	}

	@Test
	public void testSessionShutdown() {
		// http session
		HttpSession session = createSession("S2");
		HttpServletRequest request = createRequest(session);
		ServletRequestEvent requestEvent = createServletRequestEvent(request);
		HttpSessionBindingEvent event = createHttpSessionBindingEvent(session);

		// jodd
		RequestContextListener requestContextListener = new RequestContextListener();

		// start session, init request
		requestContextListener.requestInitialized(requestEvent);

		// petite
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Ses.class, null, null, null, false);

		Ses ses = (Ses) pc.getBean("ses");
		assertNotNull(ses);
		ses.setValue("jodd");

		// session not expired
		assertEquals("jodd", ses.getValue());

		// shutdown
		pc.shutdown();

		assertEquals("-jodd", ses.getValue());
	}
}
