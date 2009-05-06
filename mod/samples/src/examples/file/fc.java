// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.file;

import jodd.io.findfile.FindClass;

import java.io.File;
import java.io.InputStream;

public class fc {

	public static void main(String args[]) throws Exception {
		System.out.println("start");

		Find f = new Find();
		f.dome();
		System.out.println("end");
	}

	static class Find extends FindClass {

		public void dome() throws Exception {
			scanClassPath(new File("d://Projects/java/apps/applets"));
		}

		@Override
		protected void onClassName(String className, InputStream inputStream) {
			System.out.println("------> " + className);
		}
	}

}
