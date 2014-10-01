// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

/**
 * Http upload progress listener.
 */
public abstract class HttpProgressListener {

	/**
	 * Total size to transfer.
	 */
	protected int size;

	/**
	 * Returns callback size in bytes. By default it returns
	 * size of 1 percent of a total size. If returned size
	 * is less then 512, it will be rounded to 512.
	 * This is also the size of the chunk that is sent over network.
	 */
	public int callbackSize(int size) {
		this.size = size;

		int callbackSize = (size + 50) / 100;

		if (callbackSize < 512) {
			callbackSize = 512;
		}

		return callbackSize;
	}

	/**
	 * Callback for every sent {@link #callbackSize(int) chunk}.
	 * Also called before and after the transfer.
	 */
	public abstract void transferred(long len);

}