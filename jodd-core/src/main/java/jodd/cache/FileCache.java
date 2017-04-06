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
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Returns actually used size in bytes.
	 */
	public int getUsedSize() {
		return usedSize;
	}

	/**
	 * Returns maximum allowed file size that can be added to the cache.
	 * Files larger than this value will be not added, even if there is
	 * enough room.
	 */
	public int getMaxFileSize() {
		return maxFileSize;
	}

	/**
	 * Returns number of cached files.
	 */
	public int getCachedFilesCount() {
		return cache.size();
	}

	/**
	 * Returns timeout.
	 */
	public long getCacheTimeout() {
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
