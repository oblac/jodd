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

package jodd.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.Attribute;
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
		return MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, mBeanInterface, false);
	}

	/**
	 * Returns bean attribute.
	 */
	public Object getAttribute(final String mbeanName, final String attributeName) {
		assertConnected();
		try {
			ObjectName objectName = buildObjectName(mbeanName);
			return mbsc.getAttribute(objectName, attributeName);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
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
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
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
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
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