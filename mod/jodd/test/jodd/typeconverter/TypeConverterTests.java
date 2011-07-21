// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TypeConverterTests extends TestSuite {

	public TypeConverterTests() {
		super("jodd.typeconverter test suite");
		addTestSuite(BigDecimalConverterTest.class);
		addTestSuite(BigIntegerConverterTest.class);
		addTestSuite(BooleanArrayConverterTest.class);
		addTestSuite(BooleanConverterTest.class);
		addTestSuite(ByteArrayConverterTest.class);
		addTestSuite(ByteConverterTest.class);
		addTestSuite(CharacterConverterTest.class);
		addTestSuite(ClassArrayConverterTest.class);
		addTestSuite(ClassConverterTest.class);
		addTestSuite(DateConverterTest.class);
		addTestSuite(DoubleArrayConverterTest.class);
		addTestSuite(DoubleConverterTest.class);
		addTestSuite(FloatArrayConverterTest.class);
		addTestSuite(FloatConverterTest.class);
		addTestSuite(IntegerArrayConverterTest.class);
		addTestSuite(IntegerConverterTest.class);
		addTestSuite(JDateTimeConverterTest.class);
		addTestSuite(LongConverterTest.class);
		addTestSuite(LongArrayConverterTest.class);
		addTestSuite(MutableByteConverterTest.class);
		addTestSuite(MutableDoubleConverterTest.class);
		addTestSuite(MutableFloatConverterTest.class);
		addTestSuite(MutableIntegerConverterTest.class);
		addTestSuite(MutableLongConverterTest.class);
		addTestSuite(MutableShortConverterTest.class);
		addTestSuite(ShortArrayConverterTest.class);
		addTestSuite(ShortConverterTest.class);
		addTestSuite(SqlDateConverterTest.class);
		addTestSuite(SqlTimeConverterTest.class);
		addTestSuite(SqlTimestampConverterTest.class);
		addTestSuite(StringConverterTest.class);
		addTestSuite(StringArrayConverterTest.class);
		addTestSuite(URLConverterTest.class);
		addTestSuite(URIConverterTest.class);
	}

	public static Test suite() {
		return new TypeConverterTests();
	}
	
}
