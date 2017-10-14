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

import java.io.File;

/**
 * Cache of recently used files.
 */
public class FileLRUCache extends FileCache {

	/**
	 * Creates file LRU cache with specified size. Sets
	 * {@link #maxFileSize max available file size} to half of this value.
	 */
	public FileLRUCache(int maxSize) {
		this(maxSize, maxSize / 2, 0);
	}

	public FileLRUCache(int maxSize, int maxFileSize) {
		this(maxSize, maxFileSize, 0);
	}

	/**
	 * Creates new File LRU cache.
	 * @param maxSize total cache size in bytes
	 * @param maxFileSize max available file size in bytes, may be 0
	 * @param timeout timeout, may be 0
	 */
	public FileLRUCache(int maxSize, int maxFileSize, long timeout) {
		super(maxSize, maxFileSize, timeout);
	}

	@Override
	protected Cache<File, byte[]> createCache() {
		return new LRUCache<File, byte[]>(0, timeout) {
			@Override
			public boolean isFull() {
				return usedSize > FileLRUCache.this.maxSize;
			}

			@Override
			protected boolean isReallyFull(File file) {
				return isFull();
			}

			@Override
			protected void onRemove(File key, byte[] cachedObject) {
				usedSize -= cachedObject.length;
			}
		};
	}
}
