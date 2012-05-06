// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.Attribute;
import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Simple JMX client.
 */
public class JmxClient {

	protected JMXConnector connector;
	protected MBeanServerConnection mbsc;
	protected AtomicBoolean connected = new AtomicBoolean(false);

	public JmxClient(final String serviceUrl) throws IOException {
		initConnector(serviceUrl, null, null);
	}

	public JmxClient(final String serviceUrl, final String userName, final String passwd) throws IOException {
		initConnector(serviceUrl, userName, passwd);
	}

	/**
	 * Initializes JMX connector.
	 */
	@SuppressWarnings("unchecked")
	private void initConnector(final String serviceUrl, final String userName, final String passwd) throws IOException {
		JMXServiceURL url = new JMXServiceURL(serviceUrl);
		boolean hasCredentlals = StringUtil.isNotBlank(userName);
		if (hasCredentlals) {
			Map environment = new HashMap();
			environment.put(JMXConnector.CREDENTIALS, new String[]{userName, passwd});
			connector = JMXConnectorFactory.connect(url, environment);
		} else {
			connector = JMXConnectorFactory.connect(url);
		}
		mbsc = connector.getMBeanServerConnection();
		connected.set(true);
	}

	/**
	 * Closes JMX connector.
	 */
	public void close() throws IOException {
		connector.close();
		connected.set(false);
	}

	/**
	 * Creates MBean proxy.
	 */
	@SuppressWarnings({"unchecked"})
	public <T> T getMBeanProxy(final String mbeanName, final Class<T> mBeanInterface) {
		assertConnected();
		ObjectName objectName = buildObjectName(mbeanName);
		return (T) MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, mBeanInterface, false);
	}

	/**
	 * Returns bean attribute.
	 */
	public Object getAttribute(final String mbeanName, final String attributeName) {
		assertConnected();
		try {
			ObjectName objectName = buildObjectName(mbeanName);
			return mbsc.getAttribute(objectName, attributeName);
		} catch (JMException jmex) {
			throw new IllegalArgumentException(jmex);
		} catch (IOException ioex) {
			throw new IllegalStateException(ioex);
		}
	}

	/**
	 * Defines bean attribute.
	 */
	public void setAttribute(final String mbeanName, final String attributeName, final Object value) {
		assertConnected();
		try {
			ObjectName objectName = buildObjectName(mbeanName);
			Attribute attribute = new Attribute(attributeName, value);
			mbsc.setAttribute(objectName, attribute);
		} catch (JMException jmex) {
			throw new IllegalArgumentException(jmex);
		} catch (IOException ioex) {
			throw new IllegalStateException(ioex);
		}
	}

	/**
	 * Invokes bean method.
	 */
	public void invoke(final String mbeanName, final String methodName) {
		invoke(mbeanName, methodName, new Object[]{}, new String[]{});
	}

	/**
	 * Invokes bean method.
	 */
	public void invoke(final String mbeanName, final String methodName, final Object[] params, final String[] signature) {
		assertConnected();
		try {
			ObjectName objectName = buildObjectName(mbeanName);
			mbsc.invoke(objectName, methodName, params, signature);
		} catch (JMException jmex) {
			throw new IllegalArgumentException(jmex);
		} catch (IOException ioex) {
			throw new IllegalStateException(ioex);
		}
	}

	/**
	 * Checks if we are connected to the jmx
	 */
	protected void assertConnected() {
		if (!connected.get()) {
			throw new IllegalStateException("Not connected to JMX");
		}
	}

	/**
	 * Builds object name.
	 */
	protected ObjectName buildObjectName(final String mbeanName) {
		try {
			return new ObjectName(mbeanName);
		} catch (MalformedObjectNameException monex) {
			throw new IllegalArgumentException("Invalid mbeanName: " + mbeanName, monex);
		}
	}

}
