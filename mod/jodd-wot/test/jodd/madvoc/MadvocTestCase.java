// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import junit.framework.TestCase;

import java.lang.reflect.Method;

import jodd.util.StringUtil;
import jodd.util.ClassLoaderUtil;
import jodd.madvoc.component.ActionMethodParser;

public abstract class MadvocTestCase extends TestCase {

	protected ActionConfig parse(ActionMethodParser actionMethodParser, String signature) {
		Object[] data = resolveSignature(signature);
		return actionMethodParser.parse((Class) data[0], (Method) data[1]);
	}


	protected Object[] resolveSignature(String signature) {
		String[] data = StringUtil.splitc(signature, '#');
		try {
			data[0] = this.getClass().getPackage().getName() + '.' + data[0];
			Class c = ClassLoaderUtil.loadClass(data[0], this.getClass());
			Method m = c.getMethod(data[1]);
			return new Object[] {c, m};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
