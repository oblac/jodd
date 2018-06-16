// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.db.type;

import jodd.db.fixtures.JDBCTypesFixture;
import jodd.buffer.FastIntBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypesUtilTest {

	private static final FastIntBuffer ALL_TYPES = new FastIntBuffer(39);

	@BeforeAll
	static void beforeAll() throws Exception {
		ALL_TYPES.append(JDBCTypesFixture.getJDBCTypes());
	}

	@ParameterizedTest
	@MethodSource(value = "testData_isIntegerType")
	void testIsIntegerType(final boolean expected, final int input) {
		assertEquals(expected, TypesUtil.isIntegerType(input));
	}

	private static Collection<Arguments> testData_isIntegerType() {
		final List<Integer> integerTypes = Arrays.asList(Types.INTEGER, Types.SMALLINT, Types.TINYINT, Types.BIT);
		final List<Arguments> args = new ArrayList<>();
		Arrays.stream(ALL_TYPES.toArray())
				.forEach(intStream -> args.add(Arguments.of(integerTypes.contains(intStream), intStream)));

		return args;
	}

	@ParameterizedTest
	@MethodSource(value = "testData_isStringType")
	void testIsStringType(final boolean expected, final int input) {
		assertEquals(expected, TypesUtil.isStringType(input));
	}

	private static Collection<Arguments> testData_isStringType() {
		final List<Integer> stringTypes = Arrays.asList(Types.VARCHAR, Types.CHAR);
		final List<Arguments> args = new ArrayList<>();
		Arrays.stream(ALL_TYPES.toArray())
				.forEach(intStream -> args.add(Arguments.of(stringTypes.contains(intStream), intStream)));

		return args;
	}

}
