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

public class SmtpServerTest {

    @Test
    public void testAddsPropertyToServerSession() {
        Properties overridenProperties = new Properties();

        overridenProperties.setProperty("mail.smtp.from", "bounce@jodd.org");

        SmtpServer smtpServer = SmtpServer.create("some.host.com", 587)
                .authenticateWith("test", "password")
                .timeout(10)
                .properties(overridenProperties);

        Properties sessionProperties = smtpServer.createSession().mailSession.getProperties();

        assertEquals("bounce@jodd.org", sessionProperties.getProperty("mail.smtp.from"));
    }

    @Test
    public void testAddsPropertyToServerSession2() {
        SmtpSslServer smtpServer = SmtpSslServer.create("some.host.com", 587)
                .authenticateWith("test", "password")
                .timeout(10)
                .property(MAIL_SMTP_FROM, "bounce@jodd.org");

        Properties sessionProperties = smtpServer.createSession().mailSession.getProperties();

        assertEquals("bounce@jodd.org", sessionProperties.getProperty("mail.smtp.from"));
    }
}
