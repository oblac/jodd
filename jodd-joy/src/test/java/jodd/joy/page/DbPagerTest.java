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

package jodd.joy.page;

import jodd.joy.page.db.HsqlDbPager;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class DbPagerTest {

	class MyHsqlDbPager extends HsqlDbPager {
		public String buildCountSql2(String sql) {
			return super.buildCountSql(sql);
		}
	}

	@ParameterizedTest
	@MethodSource("sqlProvider")
	void testHsqlDbPager(String query, String expected){
		MyHsqlDbPager hsqlDbPager = new MyHsqlDbPager();

		String sql = hsqlDbPager.buildCountSql2(query);
		assertEquals(expected, sql);
	}

	static Stream<Arguments> sqlProvider() {
		return Stream.of(
				arguments("select * from User u where u.id > 10",
						"select count(*) from User u where u.id > 10"),
				arguments("select u.id, (select name from Club where...) as cname from User u where u.id > 10",
						"select count(*) from User u where u.id > 10"),
				arguments("select u.id, (select name from Club where...) as cname," +
						" (select id from Town...) as townId from User u where u.id > 10",
						"select count(*) from User u where u.id > 10"));
	}
}
