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
