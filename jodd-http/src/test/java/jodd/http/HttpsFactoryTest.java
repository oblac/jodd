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

package jodd.http;

import jodd.http.net.SSLSocketHttpConnectionProvider;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpsFactoryTest {

	@Test
	void testCustomSSLSocketHttpConnectionProvider() {
		final AtomicBoolean atomicBoolean = new AtomicBoolean();

		final SSLSocketFactory sslSocketFactory = new SSLSocketFactory() {
			@Override
			public String[] getDefaultCipherSuites() {
				return new String[0];
			}

			@Override
			public String[] getSupportedCipherSuites() {
				return new String[0];
			}

			@Override
			public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
				atomicBoolean.set(true);
				return null;
			}

			@Override
			public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
				atomicBoolean.set(true);
				return null;
			}

			@Override
			public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
				atomicBoolean.set(true);
				return null;
			}

			@Override
			public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
				atomicBoolean.set(true);
				return null;
			}

			@Override
			public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
				atomicBoolean.set(true);
				return null;
			}
		};

		try {
			HttpRequest.get("https://google.com")
				.withConnectionProvider(new SSLSocketHttpConnectionProvider(sslSocketFactory))
				.open();
		}
		catch (NullPointerException npe) {
		}

		assertTrue(atomicBoolean.get());
	}
}
