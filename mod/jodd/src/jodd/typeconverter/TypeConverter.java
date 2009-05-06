// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 * Object converter interface.
 *
 * @see jodd.typeconverter.TypeConverterManager
 */
public interface TypeConverter<T> {

	/**
	 * Converts object received as parameter into object of another class.
	 * For example, in a IntegerConverter implementation of this interface,
	 * this method should convert all objects into Integer object. Conversion has
	 * to be done correctly, with investigation of given object.
	 * <p>
	 *
	 * If conversion is not possible {@link TypeConversionException} should be thrown.
	 *
	 * @param value    object to convert from
	 *
	 * @return resulting object
	 */
	T convert(Object value);

}
