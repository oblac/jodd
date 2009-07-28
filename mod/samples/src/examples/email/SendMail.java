// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.email;

import jodd.mail.SmtpServer;
import jodd.mail.Email;
import jodd.mail.SendMailSession;
import static jodd.mail.Email.PRIORITY_HIGHEST;

public class SendMail {

	public static void main(String[] args) {
		SmtpServer smtpServer = new SmtpServer("mail.beotel.rs");
		SendMailSession session = smtpServer.createSession();

		session.open();

		Email email = Email.create()
				.from("weird@beotel.rs")
				.to("weird@beotel.rs")
				.subject("test1")
				.text("a plain text message čtf");
		session.sendMail(email);


		email.text(null)
				.subject("test2")
				.message("a <b>test 2</b> message");
		session.sendMail(email);

		email.text("and text3 message!")
				.subject("test3")
				.message("a <b>test 3</b> message");
		session.sendMail(email);


		email.subject("test4")
				.text("text 4")
				.attachFile("d:\\love_music.jpg")
				.priority(PRIORITY_HIGHEST);
		session.sendMail(email);

		session.close();

		System.out.println("done.");
	}
}
