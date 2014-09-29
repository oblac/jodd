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
	 * size of 0.5 percent of a total size. However, the callback
	 * size is limited between 10 and 512 bytes. Override this
	 * method for custom callback size.
	 */
	public int callbackSize(int size) {
		this.size = size;

		int callbackSize = size / 200;

		if (callbackSize < 10) {
			callbackSize = 10;
		}

		if (callbackSize > 512) {
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