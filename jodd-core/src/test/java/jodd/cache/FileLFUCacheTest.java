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

package jodd.cache;

import jodd.io.FileUtil;
import jodd.util.SystemUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileLFUCacheTest {

	private File tempFolder = new File(SystemUtil.tempDir());

	private File file(String fileName, int size) throws IOException {
		byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++) {
			bytes[i] = (byte) i;
		}

		File file = new File(tempFolder, fileName);
		file.deleteOnExit();

		FileUtil.writeBytes(file, bytes);

		return file;
	}

	@Test
	public void testCache() throws IOException {
		FileLFUCache cache = new FileLFUCache(25);

		assertEquals(25, cache.maxSize());
		assertEquals(12, cache.maxFileSize());

		File a = file("a", 10);
		File b = file("b", 9);
		File c = file("c", 7);

		cache.getFileBytes(a);
		cache.getFileBytes(a);
		cache.getFileBytes(a);
		cache.getFileBytes(b);

		assertEquals(2, cache.cachedFilesCount());
		assertEquals(19, cache.usedSize());

		cache.getFileBytes(c);        // b is out, a(2), c(1)

		assertEquals(2, cache.cachedFilesCount());
		assertEquals(17, cache.usedSize());

		cache.getFileBytes(c);
		cache.getFileBytes(c);
		cache.getFileBytes(c);

		cache.getFileBytes(b);        // a is out

		assertEquals(2, cache.cachedFilesCount());
		assertEquals(16, cache.usedSize());
	}
}
