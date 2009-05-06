// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

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
		try {
			return (new Character(value.toString().charAt(0)));
		} catch (IndexOutOfBoundsException ioobex) {
			throw new TypeConversionException(value, ioobex);
		}
	}

	public Character convert(Object value) {
		return valueOf(value);
	}
	
}
