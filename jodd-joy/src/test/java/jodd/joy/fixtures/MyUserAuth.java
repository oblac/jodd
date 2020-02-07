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

package jodd.joy.fixtures;

import jodd.joy.auth.AuthInterceptor;
import jodd.joy.auth.UserAuth;
import jodd.joy.auth.simtok.SimTok;
import jodd.joy.auth.simtok.SimTokCoder;
import jodd.petite.meta.PetiteBean;

@PetiteBean
public class MyUserAuth implements UserAuth<SimTok> {

	public MyUserAuth() {
		AuthInterceptor.userAuth = this;
	}

	@Override
	public SimTok login(final String principal, final String credentials) {
		if (!credentials.equals(principal + "!")) {
			return null;
		}
		return SimTok.create().setName(principal).setUid("1");
	}

	@Override
	public SimTok validateToken(final String token) {
		final SimTok simTok = new SimTokCoder().decode(token);
		if (simTok == null) {
			return null;
		}
		if (simTok.expired()) {
			return null;
		}
		return simTok;
	}

	@Override
	public SimTok rotateToken(final SimTok authToken) {
		return SimTok.from(authToken);
	}

	@Override
	public String tokenValue(final SimTok authToken) {
		return new SimTokCoder().encode(authToken);
	}
}
