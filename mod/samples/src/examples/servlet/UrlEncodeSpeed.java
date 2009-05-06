// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.servlet;

import jodd.servlet.HtmlEncoder;
import jodd.servlet.UrlEncoder;

import java.io.UnsupportedEncodingException;

public class UrlEncodeSpeed {

	public static void main(String[] args) throws UnsupportedEncodingException {

		long start = System.currentTimeMillis();
		for (int i = 1; i < 100000; i++) {
			UrlEncoder.url("/qwe?name=  dada");                 // 344
			//URLEncoder.encode("/qwe?name=  dada", "UTF-8"); //1984
		}
		System.out.println("time: " + (System.currentTimeMillis() - start) + "ms.");
	}
}
