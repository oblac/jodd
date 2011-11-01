// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import jodd.io.FileUtil;
import jodd.util.SystemUtil;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class FileLFUCacheTest extends TestCase {

	private File tempFolder = new File(SystemUtil.getTempDir());

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

	public void testCache() throws IOException {
		FileLFUCache cache = new FileLFUCache(25);

		assertEquals(25, cache.getMaxSize());
		assertEquals(12, cache.getMaxFileSize());

		File a = file("a", 10);
		File b = file("b", 9);
		File c = file("c", 7);

		cache.getFileBytes(a);
		cache.getFileBytes(a);
		cache.getFileBytes(a);
		cache.getFileBytes(b);

		assertEquals(2, cache.getCachedFilesCount());
		assertEquals(19, cache.getUsedSize());

		cache.getFileBytes(c);		// b is out, a(2), c(1)

		assertEquals(2, cache.getCachedFilesCount());
		assertEquals(17, cache.getUsedSize());

		cache.getFileBytes(c);
		cache.getFileBytes(c);
		cache.getFileBytes(c);

		cache.getFileBytes(b);		// a is out

		assertEquals(2, cache.getCachedFilesCount());
		assertEquals(16, cache.getUsedSize());
	}
}
