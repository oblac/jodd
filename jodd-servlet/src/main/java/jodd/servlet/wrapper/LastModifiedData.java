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

package jodd.servlet.wrapper;

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
