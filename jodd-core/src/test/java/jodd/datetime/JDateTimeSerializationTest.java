// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import jodd.util.ObjectUtil;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class JDateTimeSerializationTest {

	@Test
	public void testJDateTimeSerialization() throws IOException, ClassNotFoundException {
		JDateTime jDateTime = new JDateTime();

		JDateTime jDateTimeClone = ObjectUtil.cloneViaSerialization(jDateTime);

		assertNotSame(jDateTime, jDateTimeClone);

		assertEquals(jDateTime, jDateTimeClone);
		assertEquals(jDateTime.toString(), jDateTimeClone.toString());
	}
}