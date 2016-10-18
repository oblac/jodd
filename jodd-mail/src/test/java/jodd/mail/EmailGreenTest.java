package jodd.mail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class EmailGreenTest {

	@Test
	public void testSendAll() {
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
				.bytes(new byte[]{1,2,3})
				.setInline("CID1").create())
			.attach(EmailAttachment.attachment()
				.setName("two")
				.bytes(new byte[]{4,5,6})
				.setInline(false)
				.create());

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

		assertEquals(2, email.getAttachments().size());
		assertEquals("one", email.getAttachments().get(0).getName());
		assertEquals("CID1", email.getAttachments().get(0).getContentId());
		assertEquals("two", email.getAttachments().get(1).getName());

		assertEquals(2, liame.getAttachments().size());
		assertEquals(3, liame.getAttachments().get(0).getSize());
		assertEquals("one", liame.getAttachments().get(0).getName());
		assertEquals("<CID1>", liame.getAttachments().get(0).getContentId());
		assertArrayEquals(new byte[]{1,2,3}, liame.getAttachments().get(0).toByteArray());
		assertEquals(3, liame.getAttachments().get(1).getSize());
		assertEquals("two", liame.getAttachments().get(1).getName());

		greenMail.stop();
	}

}
