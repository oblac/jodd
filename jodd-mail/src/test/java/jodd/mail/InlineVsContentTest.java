package jodd.mail;

import jodd.io.FileNameUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static jodd.mail.EmailAttachment.attachment;

@Disabled("Real email sending required")
public class InlineVsContentTest {

	public static final String PNG = FileNameUtil.resolveHome("~/prj/oblac/jodd-site/content/jodd.png");

	@Test
	public void testSendEmailWithVariousAttachaments() {
		SmtpServer smtpServer = SmtpSslServer
			.create("mail.joddframework.org")
			.authenticateWith("t", "t");

		SendMailSession session = smtpServer.createSession();
		session.open();

		Email email = Email.create()
			.from("info@jodd.org")
			.to("igor.spasic@gmail.com")
			.subject("test-gmail")
			.addText("Hello!")
			.addHtml(
				"<html><META http-equiv=Content-Type content=\"text/html; charset=utf-8\">"+
					"<body><h1>Hey!</h1><img src='cid:jodd.png'>" +
					"<h2>Hay!</h2><img src='cid:jodd2.png'>" +
					"<h3></h3></body></html>")
			.embed(attachment().bytes(new File(PNG)).setInline(false))
			.embed(attachment().bytes(new File(PNG)).setContentId("jodd2.png").setInline(true))
			.attach(attachment().file(PNG))
			;

		session.sendMail(email);
		session.close();
	}
}
