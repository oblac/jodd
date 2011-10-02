// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import jodd.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Files LRU cache stores files content in memory to dramatically
 * speed up performances for frequently readed files.
 */
public class FileLRUCache {

	protected final LRUCache<File, byte[]> cache;
	protected final int maxFileSize;

	public FileLRUCache(int size, int maxFileSize) {
		this(size, maxFileSize, 0);
	}

	public FileLRUCache(int size, int maxFileSize, long timeout) {
		this.cache = new LRUCache<File, byte[]>(size, timeout);
		this.maxFileSize = maxFileSize;
	}

	/**
	 * Returns maximum file size that can be cached.
	 */
	public int getMaxFileSize() {
		return maxFileSize;
	}

	/**
	 * Returns max number of cached files.
	 */
	public int getCacheSize() {
		return cache.getCacheSize();
	}

	/**
	 * Returns timeout.
	 */
	public long getCacheTimeout() {
		return cache.getCacheTimeout();
	}

	/**
	 * Returns maximum occupated space in bytes.
	 */
	public long getMaxOccupatedSpace() {
		return maxFileSize * cache.getCacheSize();
	}

	/**
	 * Calculates actual used space.
	 */
	public long calculateUsedSpace() {
		Iterator<byte[]> iterator = cache.iterator();
		long size = 0;
		while (iterator.hasNext()) {
			byte[] next = iterator.next();
			size += next.length;
		}
		return size;
	}

	/**
	 * Clears the cache.
	 */
	public void clear() {
		cache.clear();
	}

	// ---------------------------------------------------------------- get

	public byte[] getFileBytes(String fileName) throws IOException {
		return getFileBytes(new File(fileName));
	}

	/**
	 * Returns cached file bytes.
	 */
	public byte[] getFileBytes(File file) throws IOException {
		byte[] bytes = cache.get(file);
		if (bytes != null) {
			return bytes;
		}
		bytes = FileUtil.readBytes(file);
		if (file.length() > maxFileSize) {
			return bytes;
		}
		cache.put(file, bytes);
		return bytes;
	}

}
