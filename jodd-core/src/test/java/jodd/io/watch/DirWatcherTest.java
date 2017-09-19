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

package jodd.io.watch;

import jodd.io.FileUtil;
import jodd.util.ThreadUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("test does not give consistent results - run each method individually")
public class DirWatcherTest {

	protected String dataRoot;

	@BeforeEach
	public void setUp() throws Exception {
		if (dataRoot != null) {
			return;
		}
		URL data = DirWatcherTest.class.getResource(".");
		dataRoot = data.getFile();
	}

	@Test
	public void testDirWatcher() throws IOException {
		DirWatcher dirWatcher = new DirWatcher(dataRoot, "*.md");

		final StringBuilder sb = new StringBuilder();

		dirWatcher.register(event -> sb.append(event.type().name() + ":" + event.target().getName() + "\n"));

		dirWatcher.start(100);

		File destFile = new File(dataRoot, "jodd.md");

		FileUtil.writeString(destFile, "#Jodd");
		ThreadUtil.sleep(600);

		FileUtil.writeString(destFile, "#Jodd2");
		ThreadUtil.sleep(600);

		FileUtil.delete(destFile);
		ThreadUtil.sleep(600);

		dirWatcher.stop();

		assertEquals(
				DirWatcherEvent.Type.CREATED + ":jodd.md\n" +
				DirWatcherEvent.Type.MODIFIED + ":jodd.md\n" +
				DirWatcherEvent.Type.DELETED + ":jodd.md\n",
				sb.toString());
	}

	@Test
	public void testDirWatcherWithFile() throws IOException {
		DirWatcher dirWatcher = new DirWatcher(dataRoot)
				.monitor("*.md")
				.useWatchFile("watch.txt");

		final StringBuilder sb = new StringBuilder();

		dirWatcher.register(
			event -> sb.append(event.type().name() + ":" + event.target().getName() + "\n"));

		dirWatcher.start(100);

		File watchFile = new File(dataRoot, "watch.txt");
		File destFile = new File(dataRoot, "jodd.md");

		FileUtil.writeString(destFile, "#Jodd");
		FileUtil.touch(watchFile);
		ThreadUtil.sleep(600);

		FileUtil.writeString(destFile, "#Jodd2");
		ThreadUtil.sleep(600);

		FileUtil.delete(destFile);
		FileUtil.touch(watchFile);
		ThreadUtil.sleep(600);

		dirWatcher.stop();

		assertEquals(
				DirWatcherEvent.Type.CREATED + ":jodd.md\n" +
				//DirWatcher.Event.MODIFIED + ":jodd.md\n" +
				DirWatcherEvent.Type.DELETED + ":jodd.md\n",
				sb.toString());
	}

	@Test
	public void testBlankStart() throws IOException {
		DirWatcher dirWatcher = new DirWatcher(dataRoot)
				.monitor("*.txt")
				.startBlank(true);

		final StringBuilder sb = new StringBuilder();

		dirWatcher.register(event -> {
			sb.append(event.type().name() + ":" + event.target().getName() + "\n");
		});

		dirWatcher.start(100);

		ThreadUtil.sleep(600);

		dirWatcher.stop();

		assertEquals(
				DirWatcherEvent.Type.CREATED + ":watch.txt\n",
				sb.toString());

	}

}
