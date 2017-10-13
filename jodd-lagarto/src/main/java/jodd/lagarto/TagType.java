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

package jodd.lagarto;

/**
 * Tag type.
 */
public enum TagType {

	/**
	 * Start tags: <code>&lt;foo&gt;</code>.
	 * Void tags are reported as start tags.
	 */
	START("<", ">"),

	/**
	 * End tags: <code>&lt;/foo&gt;</code>.
	 */
	END("</", ">"),

	/**
	 * Self closing tag: <code>&lt;foo/&gt;</code>.
	 */
	SELF_CLOSING("<", "/>");

	private final String startString;
	private final String endString;
	private final boolean isStarting;
	private final boolean isEnding;

	TagType(String startString, String endString) {
		this.startString = startString;
		this.endString = endString;
		isStarting = startString.length() == 1;
		isEnding = startString.length() == 2 || endString.length() == 2;
	}

	/**
	 * Returns tags starting string.
	 */
	public String getStartString() {
		return startString;
	}

	/**
	 * Returns tags ending string.
	 */
	public String getEndString() {
		return endString;
	}

	/**
	 * Returns <code>true</code> if tag is {@link #START} or {@link #SELF_CLOSING}.
	 */
	public boolean isStartingTag() {
		return isStarting;
	}

	/**
	 * Returns <code>true</code> if tag is {@link #END} or {@link #SELF_CLOSING}.
	 */
	public boolean isEndingTag() {
		return isEnding;
	}
}
