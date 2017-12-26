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

package jodd.mail;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static jodd.mail.SmtpServer.MAIL_SMTP_FROM;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SmtpServerTest {

    private static final String HOST = "some.host.com";
    private static final int PORT = 587;
    private static final String FROM = "bounce@jodd.org";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "password";

    @Test
    void testAddsPropertyToServerSession() {
        Properties overridenProperties = new Properties();

        overridenProperties.setProperty(MAIL_SMTP_FROM, FROM);

        SmtpServer smtpServer = SmtpServer.create(HOST, PORT)
                .authenticateWith(USERNAME, PASSWORD)
                .timeout(10)
                .properties(overridenProperties);

        assertFrom(smtpServer);
    }

    @Test
    void testAddsPropertyToServerSession2() {
        SmtpSslServer smtpServer = SmtpSslServer.create(HOST, PORT)
                .authenticateWith(USERNAME, PASSWORD)
                .timeout(10)
                .property(MAIL_SMTP_FROM, FROM);
        assertFrom(smtpServer);
    }

    private void assertFrom(MailServer server) {
        Properties sessionProperties = server.createSession().mailSession.getProperties();
        assertEquals(FROM, sessionProperties.getProperty(MAIL_SMTP_FROM));
    }
}
