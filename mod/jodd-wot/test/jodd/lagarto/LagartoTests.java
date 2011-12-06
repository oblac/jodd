// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.lagarto.csselly.CSSellyTest;
import jodd.lagarto.dom.DomBuilderTest;
import jodd.lagarto.dom.DomTreeTest;
import jodd.lagarto.dom.DomXmlTest;
import jodd.lagarto.dom.NodeSelectorTest;
import jodd.lagarto.dom.jerry.JerryTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class LagartoTests extends TestSuite {

	public LagartoTests() {
		super("jodd.lagarto test suite");
		addTestSuite(LagartoParserTest.class);
		addTestSuite(TagTypeTest.class);
		addTestSuite(TagAdapterTest.class);

		addTestSuite(CSSellyTest.class);

		addTestSuite(DomBuilderTest.class);
		addTestSuite(DomTreeTest.class);
		addTestSuite(NodeSelectorTest.class);
		addTestSuite(DomXmlTest.class);

		addTestSuite(JerryTest.class);
	}

	public static Test suite() {
		return new LagartoTests();
	}
}