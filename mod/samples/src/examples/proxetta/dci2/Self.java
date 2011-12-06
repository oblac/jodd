// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.dci2;

/**
 * Marker.
 */
public class Self {
	public static <T> T get() {
		throw new UnsupportedOperationException();
	}
}
