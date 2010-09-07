// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 * Converts given object to Character.
 */
public class CharacterConverter implements TypeConverter<Character> {

	public static Character valueOf(Object value) {

		if (value == null) {
			return null;
		}
		if (value instanceof Character) {
			return (Character) value;
		}
		if (value instanceof Number) {
			char c = (char) ((Number) value).intValue();
			return Character.valueOf(c);
		}
		try {
			String s = value.toString();
			if (s.length() != 1) {
				throw new TypeConversionException(value);
			}
			return new Character(s.charAt(0));
		} catch (IndexOutOfBoundsException ioobex) {
			throw new TypeConversionException(value, ioobex);
		}
	}

	public Character convert(Object value) {
		return valueOf(value);
	}
	
}
