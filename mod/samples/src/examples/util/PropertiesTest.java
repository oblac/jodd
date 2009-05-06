// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.util;

import jodd.io.AsciiInputStream;
import jodd.util.StringUtil;

import java.util.Properties;
import java.io.IOException;

public class PropertiesTest {

	public static void main(String[] args) throws IOException {
		Properties p = new Properties();
		String s = "ID:149994-11658328384691 STAT:Delivered id:28d2e34e sub:001 dlvrd:001 submit date:0612111127 done date:0612111147 err:000 text:12345678901234567890";
		s = StringUtil.replaceChar(s, ' ', '\n');
		AsciiInputStream is = new AsciiInputStream(s);
		p.load(is);
		System.out.println(p.getProperty("ID"));
		System.out.println(p.getProperty("STAT"));
	}
}
