// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.util;

import jodd.exception.UncheckedException;

public class ExceptionsPrintout {

	public static void main(String[] args) {

		//printException(new UncheckedException());

		int a = 5;
		try {
			a = a/0;
		} catch (Exception ex) {
			try {
				throw UncheckedException.wrap(ex);
			} catch (RuntimeException e) {
				throw UncheckedException.wrap(e);
			}
		}

	}

}
