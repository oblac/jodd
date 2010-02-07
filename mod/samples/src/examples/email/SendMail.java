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
		System.out.println("email #1 sent");

		email = Email.create()
				.from("weird@beotel.rs")
				.to("weird@beotel.rs")
				.subject("test2")
				.html("a <b>test 2</b> message");
		session.sendMail(email);
		System.out.println("email #2 sent");

		email = Email.create()
				.from("weird@beotel.rs")
				.to("weird@beotel.rs")
				.text("and text3 message!")
				.subject("test3")
				.html("a <b>test 3</b> message");
		session.sendMail(email);
		System.out.println("email #3 sent");

		email = Email.create()
				.from("weird@beotel.rs")
				.to("weird@beotel.rs")
				.subject("test4")
				.text("text 4")
				.attachFile("d:\\huh2.jpg")
				.priority(PRIORITY_HIGHEST);
		session.sendMail(email);
		System.out.println("email #4 sent");

		session.close();

		System.out.println("done.");
	}
}
