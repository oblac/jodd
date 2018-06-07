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

package jodd.joy.auth.simtok;

/**
 * Simple Token.
 */
public class SimTok {

	public static SimTok create() {
		return new SimTok();
	}

	public static SimTok from(final SimTok simTok) {
		return new SimTok()
			.setName(simTok.name)
			.setUid(simTok.uid);
	}

	private String name;
	private String uid;
	private long until;
	private String payload;

	public String getName() {
		return name;
	}

	public SimTok setName(final String name) {
		this.name = name;
		return this;
	}

	public String getUid() {
		return uid;
	}

	public SimTok setUid(final String uid) {
		this.uid = uid;
		return this;
	}

	public long getUntil() {
		return until;
	}

	public SimTok setUntil(final long until) {
		this.until = until;
		return this;
	}

	public SimTok setDuration(final long duration) {
		this.until = System.currentTimeMillis() + duration;
		return this;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(final String payload) {
		this.payload = payload;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Returns {@code true} if token is expired.
	 */
	public boolean expired() {
		if (until == 0) {
			return false;
		}
		return System.currentTimeMillis() > until;
	}
}
