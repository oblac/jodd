package jodd.db.type;

import jodd.util.collection.IntArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypesUtilTest {

	private static final IntArrayList list = new IntArrayList(39);
	
	@BeforeAll
	static void beforeAll() throws Exception {
		Field[] fields = Types.class.getDeclaredFields();
		for (Field field : fields) {
			final int modifiers = field.getModifiers();
			// catch all public static final int - fields
			if (Modifier.isPublic(modifiers)
					&& Modifier.isStatic(modifiers)
					&& Modifier.isFinal(modifiers)
					&& field.getType() == int.class) {
				list.add(field.getInt(field));
			}
		}
	}

	@ParameterizedTest
	@MethodSource(value = "testData_isIntegerType")
	void isIntegerType(final boolean expected, final int input) {
		assertEquals(expected, TypesUtil.isIntegerType(input));
	}
	private static Arguments[] testData_isIntegerType() {
		final List<Integer> integerTypes = Arrays.asList(Types.INTEGER , Types.SMALLINT,Types.TINYINT, Types.BIT) ;
		final List<Arguments> args = new ArrayList<>();
		Arrays.stream(list.toArray())
				.forEach(intStream -> args.add(Arguments.of(integerTypes.contains(intStream), intStream)) );

		return args.toArray(new Arguments[0]);
	}

	@ParameterizedTest
	@MethodSource(value = "testData_isStringType")
	void isStringType(final boolean expected, final int input) {
		assertEquals(expected, TypesUtil.isStringType(input));
	}
	private static Arguments[] testData_isStringType() {
		final List<Integer> stringTypes = Arrays.asList(Types.VARCHAR , Types.CHAR) ;
		final List<Arguments> args = new ArrayList<>();
		Arrays.stream(list.toArray())
				.forEach(intStream -> args.add(Arguments.of(stringTypes.contains(intStream), intStream)) );

		return args.toArray(new Arguments[0]);
	}

}