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

package jodd.util;

import jodd.io.PathUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathUtilTest {

	@Test
	public void testResolve() {
		Path base = Paths.get(fixpath("/aaa/bbb"));

		Path path = PathUtil.resolve(base, "ccc");
		assertEquals(fixpath("/aaa/bbb/ccc"), path.toString());

		path = PathUtil.resolve(base, fixpath("ccc/"));
		assertEquals(fixpath("/aaa/bbb/ccc"), path.toString());

		path = PathUtil.resolve(base, fixpath("/ccc"));
		assertEquals(fixpath("/aaa/bbb/ccc"), path.toString());

		path = PathUtil.resolve(base, "ccc", "ddd");
		assertEquals(fixpath("/aaa/bbb/ccc/ddd"), path.toString());

		path = PathUtil.resolve(base, fixpath("/ccc"), fixpath("ddd/"));
		assertEquals(fixpath("/aaa/bbb/ccc/ddd"), path.toString());

		path = PathUtil.resolve(base, fixpath("/ccc/"), fixpath("/ddd/"));
		assertEquals(fixpath("/aaa/bbb/ccc/ddd"), path.toString());
	}

	private String fixpath(String path) {
		return StringUtil.replace(path, "/", File.separator);
	}
}
