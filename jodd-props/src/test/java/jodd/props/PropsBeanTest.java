// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.bean.BeanCopy;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PropsBeanTest {

	public static class HttpConfig {
		public int port;
		public String address;
		public int pool;
	}

	@Test
	public void testInnerMapToBean() {
		String data = "http.port=10101\n" +
			"http.address=localhost\n" +
			"http.pool=30\n" +
			"foo=bar";

		Props props = new Props();
		props.load(data);

		Map innerMap = props.innerMap("http");
		assertEquals(3, innerMap.size());

		HttpConfig httpConfig = new HttpConfig();

		BeanCopy.fromMap(innerMap).toBean(httpConfig).copy();

		assertEquals(10101, httpConfig.port);
		assertEquals(30, httpConfig.pool);
		assertEquals("localhost", httpConfig.address);

		// back

		props = new Props();
		props.addInnerMap("http", innerMap);

		assertEquals("10101", props.getValue("http.port"));
		assertEquals("30", props.getValue("http.pool"));
		assertEquals("localhost", props.getValue("http.address"));
	}
}