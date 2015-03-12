// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.htmlstapler;

import org.junit.Test;

import static jodd.htmlstapler.HtmlStaplerBundlesManager.Strategy.RESOURCES_ONLY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HtmlStaplerBundlesManagerTest {

	@Test
	public void testRandomDigest() {
		HtmlStaplerBundlesManager hsbm = new HtmlStaplerBundlesManager("/ctx", "/", RESOURCES_ONLY);

		String digest = hsbm.createDigest("source");

		hsbm.setRandomDigestChars(5);

		String digest2 = hsbm.createDigest("source");
		String digest3 = hsbm.createDigest("source");

		assertEquals(digest2, digest3);
		assertTrue(digest2.startsWith(digest));


		hsbm.setRandomDigestChars(0);

		digest2 = hsbm.createDigest("source");

		assertEquals(digest, digest2);
	}
}