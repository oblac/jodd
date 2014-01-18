// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.tst.Boo;
import jodd.petite.tst.Foo;
import jodd.petite.tst.Ses;
import jodd.petite.tst.Zoo;
import jodd.servlet.RequestContextListener;
import jodd.servlet.SessionMonitor;
import org.junit.Test;

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import static jodd.petite.ServletsMockitoUtil.createHttpSessionEvent;
import static jodd.petite.ServletsMockitoUtil.createRequest;
import static jodd.petite.ServletsMockitoUtil.createServletRequestEvent;
import static jodd.petite.ServletsMockitoUtil.createSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ShutdownTest {

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
		HttpSessionEvent sessionEvent = createHttpSessionEvent(session);

		// jodd
		new SessionMonitor();
		SessionMonitor sessionMonitor = SessionMonitor.getInstance();
		RequestContextListener requestContextListener = new RequestContextListener();

		// start session, init request
		sessionMonitor.sessionCreated(sessionEvent);
		requestContextListener.requestInitialized(requestEvent);

		// petite
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Ses.class, null, null, null, false);

		Ses ses = (Ses) pc.getBean("ses");
		assertNotNull(ses);
		ses.setValue("jodd");

		// session expired
		sessionMonitor.sessionDestroyed(sessionEvent);
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
		HttpSessionEvent sessionEvent = createHttpSessionEvent(session);

		// jodd
		new SessionMonitor();
		SessionMonitor sessionMonitor = SessionMonitor.getInstance();
		RequestContextListener requestContextListener = new RequestContextListener();

		// start session, init request
		sessionMonitor.sessionCreated(sessionEvent);
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