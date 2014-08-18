// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Props141Test extends PropsTest {

	@Test
	public void test141Simple() throws IOException {
		Props props = new Props();
		String data = readDataFile("i141.props");
		props.load(data);

		assertEquals("value1", props.getValue("key1"));

		assertNull(props.getValue(".key1", "ONE"));
		assertEquals("value1#ONE", props.getValue("key1", "ONE"));
		assertEquals("value1", props.getValue("key1", "qwe", null));
	}

	@Test
	public void test141Complex() throws IOException {
		Props props = new Props();
		String data = readDataFile("i141-2.props");
		props.load(data);

		// Without profile, and using ERROR profile
		assertEquals("NOT AN ERROR 1", props.getValue("code", null));
		assertEquals("NOT AN ERROR 2", props.getValue("label", null));
		assertEquals("NOT AN ERROR 3", props.getValue("details", null));

		assertEquals("#UNDEFINED", props.getValue("code", "ERROR"));
		assertEquals("UNDEFINED LABEL", props.getValue("label", "ERROR"));
		assertEquals("UNDEFINED DETAILS", props.getValue("details", "ERROR"));

		// Using the ERROR.ONE inner profile
		assertEquals("#ONE", props.getValue("code", "ERROR.ONE"));
		assertEquals("THIS IS ERROR #ONE", props.getValue("label", "ERROR.ONE"));
		assertEquals("UNDEFINED DETAILS", props.getValue("details", "ERROR.ONE"));

		// Now, using ERROR.TWO inner profile, which uses another syntax:
		assertEquals("#TWO", props.getValue("code", "ERROR.TWO"));
		assertEquals("THIS IS ERROR #TWO", props.getValue("label", "ERROR.TWO"));
		assertEquals("UNDEFINED DETAILS", props.getValue("details", "ERROR.TWO"));

		// trying to use a third inner profile, not defined in the properties
		assertEquals("#UNDEFINED", props.getValue("code", "ERROR.THREE"));
		assertEquals("UNDEFINED LABEL", props.getValue("label", "ERROR.THREE"));
		assertEquals("UNDEFINED DETAILS", props.getValue("details", "ERROR.THREE"));
	}
}