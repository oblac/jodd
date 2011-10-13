// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora;

/**
 * Holder for last modified date. Verifies if all dispatched responses
 * updated the last-modified header. If at least one is skipped, the
 * entire request should not have this header.
 */
public class LastModifiedData {

	private long lastModified = -1;
	private int responseCount = 0;
	private int lastModifiedCount = 0;

	/**
	 * Indicates usage of new response.
	 */
	public void startNewResponse() {
		responseCount++;
	}

	/**
	 * Updates last modified date.
	 */
	public void updateLastModified(long lastModified) {
		lastModifiedCount++;
		this.lastModified = Math.max(this.lastModified, lastModified);
	}

	/**
	 * Returns last modified date or <code>-1</code>.
	 */
	public long getLastModified() {
		return lastModifiedCount == responseCount ? lastModified : -1;
	}

}
