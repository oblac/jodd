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

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import jodd.util.Base64;
import jodd.util.RandomString;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Simple encode/decoder for simple token.
 */
public class SimTokCoder {

	private static final String SECRET = "Jodd!Joy!secret!" + RandomString.get().randomAlpha(10);

	private static final int SALT_ROUNDS = 12;

	/**
	 * Encodes the {@link SimTok} to JSON string.
	 */
	public String encode(final SimTok simTok) {
		final String json = JsonSerializer.create().deep(true).serialize(simTok);

		final String p1 = Base64.encodeToString("JoddSimTok" + SALT_ROUNDS);
		final String p2 = Base64.encodeToString(json);

		final String salt = BCrypt.gensalt(SALT_ROUNDS);
		final String p3 = BCrypt.hashpw(p1 + "." + p2 + "." + SECRET, salt);

		return p1 + "." + p2 + "." + p3;
	}

	/**
	 * Decodes the String to the {@link SimTok}.
	 * Returns {@code null} if decoded token is NOT valid.
	 */
	public SimTok decode(final String token) {
		final int ndx = token.indexOf('.');
		final String p1 = token.substring(0, ndx);
		final int ndx2 = token.indexOf('.', ndx + 1);
		final String p2 = token.substring(ndx + 1, ndx2);
		final String p3 = token.substring(ndx2 + 1);

		if (!BCrypt.checkpw(p1 + "." + p2 + "." + SECRET, p3)) {
			return null;
		}

		final String p2Decoded = Base64.decodeToString(p2);
		return JsonParser.create().parse(p2Decoded, SimTok.class);
	}
}
