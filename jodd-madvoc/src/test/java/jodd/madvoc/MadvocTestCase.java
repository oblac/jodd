// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.ActionMethodParser;
import jodd.util.ClassLoaderUtil;
import jodd.util.ReflectUtil;
import jodd.util.StringUtil;

import java.lang.reflect.Method;

public abstract class MadvocTestCase {

	protected ActionConfig parse(ActionMethodParser actionMethodParser, String signature) {
		Object[] data = resolveSignature(signature);
		return actionMethodParser.parse((Class) data[0], (Method) data[1], null);
	}

	protected Object[] resolveSignature(String signature) {
		String[] data = StringUtil.splitc(signature, '#');
		try {
			data[0] = this.getClass().getPackage().getName() + '.' + data[0];
			Class c = ClassLoaderUtil.loadClass(data[0]);
			Method m = ReflectUtil.findMethod(c, data[1]);
			return new Object[]{c, m};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
