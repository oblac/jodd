// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Joins two enumerations. 
 */
public class JoinedEnumeration implements Enumeration {

	private Enumeration mOne;
	private Enumeration mTwo;

	public JoinedEnumeration(Enumeration enumeration1, Enumeration enumeration2) {
		mOne = enumeration1;
		mTwo = enumeration2;
	}

	public boolean hasMoreElements() {
		if (mOne != null) {
			if (mOne.hasMoreElements()) {
				return true;
			}
			mOne = null;
		}
		return mTwo.hasMoreElements();
	}

	public Object nextElement() {
		if (mOne != null) {
			try {
				return mOne.nextElement();
			} catch (NoSuchElementException _ex) {
				mOne = null;
			}
		}
		return mTwo.nextElement();
	}

}
