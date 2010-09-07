// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

public class CharacterConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(CharacterConverter.valueOf(null));

		assertEquals(Character.valueOf((char) 1), CharacterConverter.valueOf(Character.valueOf((char)1)));
		assertEquals(Character.valueOf((char) 1), CharacterConverter.valueOf(Integer.valueOf(1)));
		assertEquals(Character.valueOf((char) 1), CharacterConverter.valueOf(Short.valueOf((short) 1)));
		assertEquals(Character.valueOf((char) 1), CharacterConverter.valueOf(Double.valueOf(1.0D)));
		assertEquals(new Character('1'), CharacterConverter.valueOf("1"));

		try {
			CharacterConverter.valueOf("aa");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}

