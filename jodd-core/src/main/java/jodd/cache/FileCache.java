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

import java.io.File;
import java.io.IOException;

/**
 * Base in-memory files cache.
 */
public abstract class FileCache {

	protected final Cache<File, byte[]> cache;
	protected final int maxSize;
	protected final int maxFileSize;
	protected final long timeout;

	protected int usedSize;

	/**
	 * Creates new File LFU cache.
	 * @param maxSize total cache size in bytes
	 * @param maxFileSize max available file size in bytes, may be 0
	 * @param timeout timeout, may be 0
	 */
	protected FileCache(int maxSize, int maxFileSize, long timeout) {
		this.maxSize = maxSize;
		this.maxFileSize = maxFileSize;
		this.timeout = timeout;
		this.cache = createCache();
	}

	/**
	 * Creates new cache instance for files content.
	 */
	protected abstract Cache<File, byte[]> createCache();

	// ---------------------------------------------------------------- get

	/**
	 * Returns max cache size in bytes.
	 */
	public int maxSize() {
		return maxSize;
	}

	/**
	 * Returns actually used size in bytes.
	 */
	public int usedSize() {
		return usedSize;
	}

	/**
	 * Returns maximum allowed file size that can be added to the cache.
	 * Files larger than this value will be not added, even if there is
	 * enough room.
	 */
	public int maxFileSize() {
		return maxFileSize;
	}

	/**
	 * Returns number of cached files.
	 */
	public int cachedFilesCount() {
		return cache.size();
	}

	/**
	 * Returns timeout.
	 */
	public long cacheTimeout() {
		return cache.timeout();
	}

	/**
	 * Clears the cache.
	 */
	public void clear() {
		cache.clear();
		usedSize = 0;
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns cached file bytes. If file is not cached it will be
	 * read and put in the cache (if all the rules are satisfied).
	 */
	public byte[] getFileBytes(File file) throws IOException {
		byte[] bytes = cache.get(file);
		if (bytes != null) {
			return bytes;
		}

		// add file
		bytes = FileUtil.readBytes(file);

		if ((maxFileSize != 0) && (file.length() > maxFileSize)) {
			// don't cache files that size exceed max allowed file size
			return bytes;
		}

		usedSize += bytes.length;

		// put file into cache
		// if used size > total, purge() will be invoked
		cache.put(file, bytes);

		return bytes;
	}

}