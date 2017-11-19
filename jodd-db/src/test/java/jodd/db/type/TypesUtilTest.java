package jodd.db.type;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class TypesUtilTest {

	@ParameterizedTest
	@CsvSource({
			"true, 4", "true, 5", "true, -6", "true, -7",
			"false, 12", "false, 1",
	})
	void isIntegerType(final boolean expected, final int input) {
		assertEquals(expected, TypesUtil.isIntegerType(input));
	}

	@ParameterizedTest
	@CsvSource({
			"true, 12", "true, 1",
			"false, 2", "false, 2011", "false, -16"
	})
	void isStringType(final boolean expected, final int input) {
		assertEquals(expected, TypesUtil.isStringType(input));
	}

}