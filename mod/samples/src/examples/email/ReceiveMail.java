// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package examples.email;

import jodd.format.Printf;
import jodd.io.FileUtil;
import jodd.mail.EmailMessage;
import jodd.mail.Pop3Server;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import jodd.mail.SimpleAuthenticator;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ReceiveMail {

	public static void main(String[] args) throws IOException {
		Pop3Server popServer = new Pop3Server("pop3.beotel.yu", new SimpleAuthenticator("weird", "be0netweird1"));
		ReceiveMailSession session = popServer.createSession();
		session.open();
		System.out.println(session.getMessageCount());
		ReceivedEmail[] emails = session.receiveEmail(false);
		if (emails != null) {
			for (ReceivedEmail email : emails) {
				System.out.println("\n\n===[" + email.getMessageNumber() + "]==================================");
				Printf.out("%0x", email.getFlags());
				System.out.println(email.getFrom());
				System.out.println(email.getTo()[0]);
				System.out.println(email.getSubject());
				System.out.println(email.getPriority());
				System.out.println(email.getSentDate());
				System.out.println(email.getReceiveDate());
				LinkedList<EmailMessage> messages = email.getAllMessages();
				for (EmailMessage msg : messages) {
					System.out.println("------------------------------------");
					System.out.println(msg.getEncoding());
					System.out.println(msg.getMimeType());
					System.out.println(msg.getContent());
				}
				HashMap<String, byte[]> map = email.getAttachments();
				if (map != null) {
					System.out.println("++++++++++++++++++++++++++++++++++");
					for (Map.Entry<String, byte[]> at : map.entrySet()) {
						System.out.println(at.getKey());
						FileUtil.writeBytes("d:\\xxx.png", at.getValue());
					}
				}
				
			}
		}
		session.close();
		
	}
}
