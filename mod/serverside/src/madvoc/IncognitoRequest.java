// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;


/**
 * Example of a non-annotated class that was manually defined as an madvoc action.
 * Class name doesn't ends with 'Action', there are no annotations etc.
 */
public class IncognitoRequest {

	/**
	 * Mapped to '/incognito.html'
	 */
	public String hello() {
		System.out.println("IncognitoRequest.hello");
		return "ok";
	}
}
