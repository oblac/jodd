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

package jodd.typeconverter;

import jodd.system.SystemUtil;
import jodd.typeconverter.impl.URLConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class URLConverterTest {

	@Test
	void testConvert_with_null_input() {
		final URLConverter urlConverter = new URLConverter();

		final URL actual = urlConverter.convert(null);

		//asserts
		assertNull(actual);
	}

	@ParameterizedTest
	@MethodSource("testdata_testConvert_with_non_null_input")
	void testConvert_with_non_null_input(final Object input) {
		final URLConverter urlConverter = new URLConverter();

		final URL actual = urlConverter.convert(input);

		// asserts
		assertNotNull(actual);
	}

	private static Collection<Arguments> testdata_testConvert_with_non_null_input() throws Exception{

		final List<Arguments> params = new ArrayList<>();

		params.add(Arguments.of(new URL("http://jodd.org/")));
		params.add(Arguments.of(new File(SystemUtil.info().getTempDir(), "jodd.txt")));
		params.add(Arguments.of(new File(SystemUtil.info().getTempDir(), "jodd.txt").toURI()));
		params.add(Arguments.of("http://jodd.org/"));

		return params;
	}

}
