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
 * Implementation of {@link jodd.lagarto.Doctype} used during parsing.
 * Only one instance is created per parsing and it is going to be reused.
 */
public class ParsedDoctype implements Doctype {

	protected CharSequence name;
	protected CharSequence publicIdentifier;
	protected CharSequence systemIdentifier;
	protected boolean quirksMode;

	public void setName(final CharSequence name) {
		this.name = name;
	}

	public void setQuirksMode(final boolean quirksMode) {
		this.quirksMode = quirksMode;
	}

	public void reset() {
		name = null;
		quirksMode = false;
		publicIdentifier = null;
		systemIdentifier = null;
	}

	public void setPublicIdentifier(final CharSequence publicIdentifier) {
		this.publicIdentifier = publicIdentifier;
	}

	public void setSystemIdentifier(final CharSequence systemIdentifier) {
		this.systemIdentifier = systemIdentifier;
	}

	// ---------------------------------------------------------------- get

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharSequence getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isQuirksMode() {
		return quirksMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharSequence getPublicIdentifier() {
		return publicIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharSequence getSystemIdentifier() {
		return systemIdentifier;
	}

}