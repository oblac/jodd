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

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailGreenTest {

	@Test
	public void testInlineAttachmentAfterSending() {
		GreenMail greenMail = new GreenMail(ServerSetupTest.ALL);
		greenMail.setUser("green@mail.com", "green", "pwd");
		greenMail.start();

		// make

		Email email = Email.create()
			.from("Jodd", "jodd@use.me")
			.to("green@mail.com")
			.addText("Hello")
			.addHtml("Hi!")
			.attach(EmailAttachment.attachment()
				.setName("one")
				.bytes(new byte[]{7,8,9})
				.setInline(false)
				.create())
			.attach(EmailAttachment.attachment()
				.setName("two")
				.bytes(new byte[]{4,5,6})
				.setInline(false)
				.create())
			.attach(EmailAttachment.attachment()
				.setName("three")
				.bytes(new byte[]{1,2,3})
				.setContentId("CID1").create())
			.embed(EmailAttachment.attachment()
				.bytes(new byte[]{0,1,0}))
			;

		assertEquals(4, email.getAttachments().size());

		// send

		{
			SmtpServer smtpServer = new SmtpServer("localhost", 3025);
			SendMailSession session = smtpServer.createSession();
			session.open();
			session.sendMail(email);
			session.close();
		}

		// receive
		ReceivedEmail[] receivedEmails;

		{
			Pop3Server popServer = new Pop3Server("localhost", 3110, "green", "pwd");
			ReceiveMailSession session = popServer.createSession();
			session.open();
			receivedEmails = session.receiveEmail();
			session.close();
		}

		assertEquals(1, receivedEmails.length);
		ReceivedEmail liame = receivedEmails[0];

		// asserts
		assertEquals("Jodd <jodd@use.me>", email.getFrom().toString());
		assertEquals("Jodd <jodd@use.me>", liame.getFrom().toString());

		assertEquals("green@mail.com", email.getTo()[0].toString());
		assertEquals("green@mail.com", liame.getTo()[0].toString());

		assertEquals(4, email.getAttachments().size());
		assertEquals("one", email.getAttachments().get(0).getName());
		assertArrayEquals(new byte[]{7,8,9}, email.getAttachments().get(0).toByteArray());
		assertEquals("two", email.getAttachments().get(1).getName());
		assertArrayEquals(new byte[]{4,5,6}, email.getAttachments().get(1).toByteArray());
		assertEquals("three", email.getAttachments().get(2).getName());
		assertEquals("CID1", email.getAttachments().get(2).getContentId());
		assertArrayEquals(new byte[]{1,2,3}, email.getAttachments().get(2).toByteArray());
		assertTrue(email.getAttachments().get(2).isEmbedded());
		assertNull(email.getAttachments().get(3).getName());
		assertArrayEquals(new byte[]{0,1,0}, email.getAttachments().get(3).toByteArray());
		assertFalse(email.getAttachments().get(3).isEmbedded());

		assertEquals(4, liame.getAttachments().size());
		int ndx = 1;
		assertEquals("one", liame.getAttachments().get(ndx).getName());
		assertArrayEquals(new byte[]{7,8,9}, liame.getAttachments().get(ndx).toByteArray());

		ndx = 2;
		assertEquals("two", liame.getAttachments().get(ndx).getName());
		assertArrayEquals(new byte[]{4,5,6}, liame.getAttachments().get(ndx).toByteArray());

		ndx = 3;
		assertEquals("three", liame.getAttachments().get(ndx).getName());
		assertEquals("<CID1>", liame.getAttachments().get(ndx).getContentId());
		assertArrayEquals(new byte[]{1,2,3}, liame.getAttachments().get(ndx).toByteArray());
		assertTrue(liame.getAttachments().get(ndx).isEmbedded());

		ndx = 0;
		assertNotNull(liame.getAttachments().get(ndx).getName());
		assertArrayEquals(new byte[]{0,1,0}, liame.getAttachments().get(ndx).toByteArray());
		assertFalse(liame.getAttachments().get(ndx).isEmbedded());

		greenMail.stop();
	}

}
