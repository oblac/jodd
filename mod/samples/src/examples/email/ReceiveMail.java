// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.email;

import jodd.format.Printf;
import jodd.io.FileUtil;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import jodd.mail.Pop3Server;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import jodd.mail.SimpleAuthenticator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReceiveMail {

	public static void main(String[] args) throws IOException {
		Pop3Server popServer = new Pop3Server("pop3.beotel.net", new SimpleAuthenticator("weird", "..."));
		ReceiveMailSession session = popServer.createSession();
		session.open();
		System.out.println(session.getMessageCount());
		ReceivedEmail[] emails = session.receiveEmail(false);
		session.close();

		if (emails != null) {
			for (ReceivedEmail email : emails) {
				System.out.println("\n\n===[" + email.getMessageNumber() + "]===========================================");
				Printf.out("%0x", email.getFlags());
				System.out.println("FROM:" + email.getFrom());
				System.out.println("TO:" + email.getTo()[0]);
				System.out.println("SUBJECT:" + email.getSubject());
				System.out.println("PRIORITY:" + email.getPriority());
				System.out.println("SENT DATE:" + email.getSentDate());
				System.out.println("RECEIVED DATE: " + email.getReceiveDate());

				LinkedList<EmailMessage> messages = email.getAllMessages();
				for (EmailMessage msg : messages) {
					System.out.print("---msg---------------------------------");
					System.out.println(msg.getEncoding() + ';' + msg.getMimeType());
					System.out.println(msg.getContent());
				}

				List<EmailAttachment> attachments = email.getAttachments();
				if (attachments != null) {
					System.out.println("+++att+++++++++++++++++++++++++++++++");
					for (EmailAttachment attachment : attachments) {
						System.out.println("name: " + attachment.getName());
						System.out.println("cid: " + attachment.getContentId());
						System.out.println("size: " + attachment.getSize());
						attachment.writeToFile(new File("d:\\", attachment.getName()));
					}
				}
				
			}
		}


	}
}
