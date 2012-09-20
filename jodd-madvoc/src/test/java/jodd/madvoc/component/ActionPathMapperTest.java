// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.MadvocTestCase;
import jodd.madvoc.WebApplication;

public class ActionPathMapperTest extends MadvocTestCase {

	public void testMapping() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionPathMapper mapper = webapp.getComponent(ActionPathMapper.class);

		String signature = mapper.mapActionPathToSignature("/foo.boo.html", "com.zoo");
		assertEquals("com.zoo.Foo#boo", signature);
		signature = mapper.mapActionPathToSignature("foo.boo.html", "com.zoo");
		assertEquals("com.zoo.Foo#boo", signature);

		signature = mapper.mapActionPathToSignature("/foo.jpg", "com.zoo");
		assertEquals("com.zoo.Foo#viewJpg", signature);

		signature = mapper.mapActionPathToSignature("/foo.html", "com.zoo");
		assertEquals("com.zoo.Foo#view", signature);

		signature = mapper.mapActionPathToSignature("/foo", "com.zoo");
		assertEquals("com.zoo.Foo#execute", signature);



		signature = mapper.mapActionPathToSignature("/doo/foo.boo.html", "com.zoo");
		assertEquals("com.zoo.doo.Foo#boo", signature);

		signature = mapper.mapActionPathToSignature("/doo/foo.jpg", "com.zoo");
		assertEquals("com.zoo.doo.Foo#viewJpg", signature);

		signature = mapper.mapActionPathToSignature("/doo/foo.html", "com.zoo");
		assertEquals("com.zoo.doo.Foo#view", signature);

		signature = mapper.mapActionPathToSignature("/doo/foo", "com.zoo");
		assertEquals("com.zoo.doo.Foo#execute", signature);

	}

}
