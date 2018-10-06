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

package jodd.db.jtx;

import jodd.db.DbTransactionMode;
import jodd.jtx.JtxIsolationLevel;
import jodd.jtx.JtxPropagationBehavior;
import jodd.jtx.JtxTransactionMode;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JtxDbUtilTest {

	@ParameterizedTest
	@MethodSource(value = "testData_testConvertToDbMode")
	void testConvertToDbMode(final DbTransactionMode expected, final JtxTransactionMode input) {

		final DbTransactionMode actual = JtxDbUtil.convertToDbMode(input);

		// asserts
		assertNotNull(actual);
		assertTrue(expected.equals(actual));
	}

	private static Collection<Arguments> testData_testConvertToDbMode() {

		final List<Arguments> params = new ArrayList<>();

		// ISOLATION_DEFAULT
		{
			final boolean readonly = RandomUtils.nextBoolean();

			JtxTransactionMode jtxTransactionMode = new JtxTransactionMode(
				JtxPropagationBehavior.PROPAGATION_REQUIRED,
				JtxIsolationLevel.ISOLATION_DEFAULT,
				readonly,
				JtxTransactionMode.DEFAULT_TIMEOUT);

			DbTransactionMode dbTransactionMode = new DbTransactionMode(DbTransactionMode.ISOLATION_DEFAULT, readonly);

			params.add(Arguments.of(dbTransactionMode, jtxTransactionMode));
		}

		// ISOLATION_NONE
		{
			final boolean readonly = RandomUtils.nextBoolean();

			JtxTransactionMode jtxTransactionMode = new JtxTransactionMode(
				JtxPropagationBehavior.PROPAGATION_REQUIRED,
				JtxIsolationLevel.ISOLATION_NONE,
				readonly,
				JtxTransactionMode.DEFAULT_TIMEOUT);

			DbTransactionMode dbTransactionMode = new DbTransactionMode(DbTransactionMode.ISOLATION_NONE, readonly);

			params.add(Arguments.of(dbTransactionMode, jtxTransactionMode));
		}

		// ISOLATION_READ_COMMITTED
		{
			final boolean readonly = RandomUtils.nextBoolean();

			JtxTransactionMode jtxTransactionMode = new JtxTransactionMode(
				JtxPropagationBehavior.PROPAGATION_REQUIRED,
				JtxIsolationLevel.ISOLATION_READ_COMMITTED,
				readonly,
				JtxTransactionMode.DEFAULT_TIMEOUT);

			DbTransactionMode dbTransactionMode = new DbTransactionMode(DbTransactionMode.ISOLATION_READ_COMMITTED, readonly);

			params.add(Arguments.of(dbTransactionMode, jtxTransactionMode));
		}

		// ISOLATION_READ_UNCOMMITTED
		{
			final boolean readonly = RandomUtils.nextBoolean();

			JtxTransactionMode jtxTransactionMode = new JtxTransactionMode(
				JtxPropagationBehavior.PROPAGATION_REQUIRED,
				JtxIsolationLevel.ISOLATION_READ_UNCOMMITTED,
				readonly,
				JtxTransactionMode.DEFAULT_TIMEOUT);

			DbTransactionMode dbTransactionMode = new DbTransactionMode(DbTransactionMode.ISOLATION_READ_UNCOMMITTED,readonly);

			params.add(Arguments.of(dbTransactionMode, jtxTransactionMode));
		}

		// ISOLATION_REPEATABLE_READ
		{
			final boolean readonly = RandomUtils.nextBoolean();

			JtxTransactionMode jtxTransactionMode = new JtxTransactionMode(
				JtxPropagationBehavior.PROPAGATION_REQUIRED,
				JtxIsolationLevel.ISOLATION_REPEATABLE_READ,
				readonly,
				JtxTransactionMode.DEFAULT_TIMEOUT);

			DbTransactionMode dbTransactionMode = new DbTransactionMode(DbTransactionMode.ISOLATION_REPEATABLE_READ, readonly);

			params.add(Arguments.of(dbTransactionMode, jtxTransactionMode));
		}

		// ISOLATION_SERIALIZABLE
		{
			final boolean readonly = RandomUtils.nextBoolean();

			JtxTransactionMode jtxTransactionMode = new JtxTransactionMode(
				JtxPropagationBehavior.PROPAGATION_REQUIRED,
				JtxIsolationLevel.ISOLATION_SERIALIZABLE,
				readonly,
				JtxTransactionMode.DEFAULT_TIMEOUT);

			DbTransactionMode dbTransactionMode = new DbTransactionMode(DbTransactionMode.ISOLATION_SERIALIZABLE, readonly);

			params.add(Arguments.of(dbTransactionMode, jtxTransactionMode));
		}

		return params;
	}

}