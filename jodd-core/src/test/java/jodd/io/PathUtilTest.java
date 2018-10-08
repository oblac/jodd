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

package jodd.io;

import jodd.system.SystemUtil;
import jodd.util.RandomString;
import jodd.util.StringUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.FileSystemException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class PathUtilTest {

	static final File BASE_DIR = new File(SystemUtil.info().getTempDir(), "jodd/" + PathUtilTest.class.getSimpleName());

	@BeforeAll
	public static void beforeAll() throws Exception {
		if (BASE_DIR.exists()) {
			// clean up all subdirs & files
			Files.walk(BASE_DIR.toPath(), FileVisitOption.FOLLOW_LINKS)
					.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.peek(System.out::println)
					.forEach(File::delete);
		}
		// created directory is needed for tests
		BASE_DIR.mkdirs();
	}

	@Test
	void testResolve() {
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

	@Nested
	@DisplayName("tests for PathUtil#readString")
	class ReadString {

		@Test
		void testReadString_with_unknown_path() throws Exception {

			assertThrows(IOException.class, () -> {
			   PathUtil.readString(new File(BASE_DIR, RandomString.get().randomAlpha(8)).toPath());
			});
		}

		@Test
		void testReadString_with_new_file() throws Exception {

			final String expected = "üöä ÜÖÄ ß";

			File file = new File(BASE_DIR, "file_with_german_umlaut.txt");

			FileUtil.writeString(file, expected, "UTF-8");

			final String actual = PathUtil.readString(file.toPath());

			// asserts
			assertEquals(expected, actual);
		}

	}

	@Nested
	@DisplayName("tests for PathUtil#deleteFileTree")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class DeleteFileTree {

		File baseDir_Successful = new File(BASE_DIR, "DeleteFileTree_Succesful");
		File baseDir_Not_Successful = new File(BASE_DIR, "DeleteFileTree_Not_Succesful");
		File locked_file = new File(baseDir_Not_Successful, "abc/jodd.lock");

		@BeforeAll
		public void beforeAll() throws Exception {

			// setup for successful deletion of paths
			{
				baseDir_Successful.mkdirs();
				Files.createDirectories(new File(baseDir_Successful, "ggg/hhh/").toPath());
				FileUtil.touch(new File(baseDir_Successful, "ggg/hhh/hello.txt"));
				FileUtil.touch(new File(baseDir_Successful, "jodd.makes.fun"));
			}

			// setup for non successful deletion of paths
			{
				baseDir_Not_Successful.mkdirs();
				Files.createDirectories(new File(baseDir_Not_Successful, "abc/def").toPath());
				FileUtil.touch(locked_file);
			}
		}

		@Test
		void testDeleteFileTree_successful() throws Exception {
			assumeTrue(baseDir_Successful.exists());

			PathUtil.deleteFileTree(baseDir_Successful.toPath());

			// asserts
			assertFalse(baseDir_Successful.exists());
		}

		@Test
		@EnabledOnOs({OS.WINDOWS})  // on windows host, test is successful. on linux host no io-exception is thrown
		void testDeleteFileTree_not_successful() throws Exception {
			assumeTrue(baseDir_Not_Successful.exists());
			assumeTrue(locked_file.exists());

			// When you use FileLock, it is purely advisory—acquiring a lock on a file may not stop you from doing anything:
			// reading, writing, and deleting a file may all be possible even when another process has acquired a lock.
			// Sometimes, a lock might do more than this on a particular platform, but this behavior is unspecified,
			// and relying on more than is guaranteed in the class documentation is a recipe for failure.

			try (RandomAccessFile randomAccessFile = new RandomAccessFile(locked_file, "rw");
				 FileLock lock = randomAccessFile.getChannel().lock())
			{
				assumeTrue(lock.isValid(), locked_file.getAbsolutePath() + " is NOT locked...");

				// asserts
				IOException expectedException = assertThrows(
					IOException.class, () -> PathUtil.deleteFileTree(baseDir_Not_Successful.toPath()));
				assertTrue(expectedException instanceof FileSystemException);
				assertEquals(locked_file.getAbsolutePath(), ((FileSystemException)expectedException).getFile());
			}
		}
	}

}
