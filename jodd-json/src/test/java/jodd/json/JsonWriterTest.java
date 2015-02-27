// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonWriterTest {

	@Test
	public void testJsonWriter() {
		StringBuilder sb = new StringBuilder();
		JsonWriter jsonWriter = new JsonWriter(sb);

		jsonWriter.writeOpenObject();
		jsonWriter.writeName("one");
		jsonWriter.writeNumber(Long.valueOf(123));
		jsonWriter.writeComma();
		jsonWriter.writeName("two");
		jsonWriter.writeString("UberLight");
		jsonWriter.writeCloseObject();

		assertEquals("{\"one\":123,\"two\":\"UberLight\"}", sb.toString());
	}
}