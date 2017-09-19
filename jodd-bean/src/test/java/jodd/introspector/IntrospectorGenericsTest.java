// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.introspector;

import jodd.util.ClassUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IntrospectorGenericsTest {

	public static class MethodParameterType<A> {
		List<A> f;
		List f2;
		Map<String, A>f3;
		List<Long> f4;
		<T extends List<T>> void m(A a, String p1, T p2, List<?> p3, List<T> p4) { }
		<T extends List<T>> List<T> m2(A a, String p1, T p2, List<?> p3, List<T> p4) { return null; }
		<T extends List<T>> List<A> m3(A a, String p1, T p2, List<?> p3, List<T> p4) { return null; }
		List<Byte> m4(List<Long> list) {return null;}
		List<A> m5(List<A> list) {return null;}
	}

	public static class Foo extends MethodParameterType<Integer> {}

	@Test
	public void testFields() throws NoSuchFieldException {
		ClassDescriptor cd = ClassIntrospector.lookup(MethodParameterType.class);

		assertEquals(MethodParameterType.class, cd.getType());
		assertEquals(4, cd.getAllFieldDescriptors().length);

		FieldDescriptor[] fs = cd.getAllFieldDescriptors();
		int p = 0;
		for (FieldDescriptor f : fs) {
			if (f.isPublic()) {
				p++;
			}
		}
		assertEquals(0, p);

		FieldDescriptor fd = cd.getFieldDescriptor("f", true);
		FieldDescriptor fd2 = cd.getFieldDescriptor("f2", true);
		FieldDescriptor fd3 = cd.getFieldDescriptor("f3", true);
		FieldDescriptor fd4 = cd.getFieldDescriptor("f4", true);

		assertEquals(List.class, fd.getRawType());
		assertEquals(Object.class, fd.getRawComponentType());

		assertEquals(List.class, fd2.getRawType());
		assertNull(fd2.getRawComponentType());

		assertEquals(Map.class, fd3.getRawType());
		assertEquals(Object.class, fd3.getRawComponentType());

		assertEquals(List.class, fd4.getRawType());
		assertEquals(Long.class, fd4.getRawComponentType());

		// impl
		cd = ClassIntrospector.lookup(Foo.class);

		fd = cd.getFieldDescriptor("f", true);
		fd2 = cd.getFieldDescriptor("f2", true);
		fd3 = cd.getFieldDescriptor("f3", true);

		assertEquals(List.class, fd.getRawType());
		assertEquals(Integer.class, fd.getRawComponentType());

		assertEquals(List.class, fd2.getRawType());
		assertNull(fd2.getRawComponentType());

		assertEquals(Map.class, fd3.getRawType());
		assertEquals(Integer.class, fd3.getRawComponentType());
		assertEquals(String.class, ClassUtil.getComponentTypes(fd3.getField().getGenericType(), cd.getType())[0]);
	}

	@Test
	public void testMethods() throws NoSuchMethodException {
		ClassDescriptor cd = ClassIntrospector.lookup(MethodParameterType.class);

		assertEquals(MethodParameterType.class, cd.getType());
		assertEquals(5, cd.getAllMethodDescriptors().length);

		MethodDescriptor[] mds = cd.getAllMethodDescriptors();
		int mc = 0;
		for (MethodDescriptor md : mds) {
			if (md.isPublic()) mc++;
		}
		assertEquals(0, mc);


		Class[] params = new Class[] {Object.class, String.class, List.class, List.class, List.class};

		Method m = MethodParameterType.class.getDeclaredMethod("m", params);
		assertNotNull(m);

		Method m2 = cd.getMethodDescriptor("m", params, true).getMethod();
		assertNotNull(m2);
		assertEquals(m, m2);

		MethodDescriptor md1 = cd.getMethodDescriptor("m", params, true);
		assertNotNull(md1);
		assertEquals(m, md1.getMethod());
		assertArrayEquals(params, md1.getRawParameterTypes());
		assertEquals(void.class, md1.getRawReturnType());
		assertNull(md1.getRawReturnComponentType());

		MethodDescriptor md2 = cd.getMethodDescriptor("m2", params, true);
		assertNotNull(md2);
		assertArrayEquals(params, md2.getRawParameterTypes());
		assertEquals(List.class, md2.getRawReturnType());
		assertEquals(List.class, md2.getRawReturnComponentType());

		MethodDescriptor md3 = cd.getMethodDescriptor("m3", params, true);
		assertNotNull(md3);
		assertArrayEquals(params, md3.getRawParameterTypes());
		assertEquals(List.class, md3.getRawReturnType());
		assertEquals(Object.class, md3.getRawReturnComponentType());

		MethodDescriptor md4 = cd.getMethodDescriptor("m4", new Class[] {List.class}, true);
		assertNotNull(md4);
		assertArrayEquals(new Class[] {List.class}, md4.getRawParameterTypes());
		assertEquals(List.class, md4.getRawReturnType());
		assertEquals(Byte.class, md4.getRawReturnComponentType());
		assertEquals(List.class, md4.getSetterRawType());
		assertEquals(Long.class, md4.getSetterRawComponentType());

		MethodDescriptor md5 = cd.getMethodDescriptor("m5", new Class[] {List.class}, true);
		assertNotNull(md5);
		assertArrayEquals(new Class[] {List.class}, md5.getRawParameterTypes());
		assertEquals(List.class, md5.getRawReturnType());
		assertEquals(Object.class, md5.getRawReturnComponentType());
		assertEquals(List.class, md5.getSetterRawType());
		assertEquals(Object.class, md5.getSetterRawComponentType());


		// impl

		Class[] params2 = new Class[] {Integer.class, String.class, List.class, List.class, List.class};

		ClassDescriptor cd1 = ClassIntrospector.lookup(Foo.class);

		assertEquals(0, Foo.class.getDeclaredMethods().length);

		MethodDescriptor[] allm = cd1.getAllMethodDescriptors();

		assertEquals(5, allm.length);

		md3 = cd1.getMethodDescriptor("m", params, true);
		assertNotNull(md3);

		assertArrayEquals(params2, md3.getRawParameterTypes());

		md3 = cd1.getMethodDescriptor("m3", params, true);
		assertNotNull(md3);
		assertArrayEquals(params2, md3.getRawParameterTypes());
		assertEquals(List.class, md3.getRawReturnType());
		assertEquals(Integer.class, md3.getRawReturnComponentType());

		md5 = cd1.getMethodDescriptor("m5", new Class[] {List.class}, true);
		assertNotNull(md5);
		assertArrayEquals(new Class[] {List.class}, md5.getRawParameterTypes());
		assertEquals(List.class, md5.getRawReturnType());
		assertEquals(Integer.class, md5.getRawReturnComponentType());
		assertEquals(List.class, md5.getSetterRawType());
		assertEquals(Integer.class, md5.getSetterRawComponentType());
	}
}
