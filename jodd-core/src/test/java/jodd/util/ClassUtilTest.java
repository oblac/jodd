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

package jodd.util;

import jodd.util.fixtures.subclass.IBase;
import jodd.util.fixtures.subclass.IExtra;
import jodd.util.fixtures.subclass.IOne;
import jodd.util.fixtures.subclass.ITwo;
import jodd.util.fixtures.subclass.SBase;
import jodd.util.fixtures.subclass.SOne;
import jodd.util.fixtures.subclass.STwo;
import jodd.util.fixtures.testdata.A;
import jodd.util.fixtures.testdata.B;
import jodd.util.fixtures.testdata.C;
import jodd.util.fixtures.testdata.JavaBean;
import jodd.util.fixtures.testdata2.D;
import jodd.util.fixtures.testdata2.E;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ClassUtilTest {

	@Test
	public void testMethod0() {
		TFooBean bean = new TFooBean();
		Method m;
		m = ClassUtil.getMethod0(TFooBean.class, "getMore", String.class, Integer.class);
		assertNotNull(m);

		m = ClassUtil.getMethod0(bean.getClass(), "getMore", String.class, Integer.class);
		assertNotNull(m);

		m = ClassUtil.getMethod0(bean.getClass(), "getXXX", String.class, Integer.class);
		assertNull(m);

		m = ClassUtil.getMethod0(bean.getClass(), "getPublic");
		assertNotNull(m);

		m = ClassUtil.getMethod0(bean.getClass(), "getDefault");
		assertNull(m);

		m = ClassUtil.getMethod0(bean.getClass(), "getProtected");
		assertNull(m);

		m = ClassUtil.getMethod0(bean.getClass(), "getPrivate");
		assertNull(m);
	}


	@Test
	public void testMethod() {
		TFooBean bean = new TFooBean();
		Method m;
		m = ClassUtil.findMethod(TFooBean.class, "getMore");
		assertNotNull(m);

		m = ClassUtil.findMethod(bean.getClass(), "getMore");
		assertNotNull(m);

		m = ClassUtil.findMethod(bean.getClass(), "getXXX");
		assertNull(m);
	}


	@Test
	public void testMatchClasses() {
		TFooBean a = new TFooBean();
		TFooBean b = new TFooBean();
		TFooBean2 c = new TFooBean2();

		assertTrue(TFooBean.class.isInstance(a));
		assertTrue(ClassUtil.isTypeOf(TFooBean.class, a.getClass()));
		assertTrue(ClassUtil.isTypeOf(TFooBean.class, b.getClass()));
		assertTrue(ClassUtil.isTypeOf(a.getClass(), b.getClass()));
		assertTrue(ClassUtil.isTypeOf(b.getClass(), a.getClass()));

		assertTrue(ClassUtil.isTypeOf(TFooBean2.class, c.getClass()));
		assertTrue(ClassUtil.isTypeOf(TFooBean2.class, TFooBean.class));
		assertFalse(ClassUtil.isTypeOf(TFooBean.class, TFooBean2.class));
		assertTrue(ClassUtil.isTypeOf(c.getClass(), TFooBean.class));
		assertFalse(ClassUtil.isTypeOf(a.getClass(), TFooBean2.class));

		assertTrue(ClassUtil.isTypeOf(TFooBean.class, Serializable.class));
		assertTrue(Serializable.class.isInstance(c));
		//noinspection ConstantConditions
		assertTrue(c instanceof Serializable);
		assertTrue(ClassUtil.isInstanceOf(c, Serializable.class));
		assertTrue(ClassUtil.isTypeOf(TFooBean2.class, Serializable.class));
		assertTrue(ClassUtil.isTypeOf(TFooBean2.class, Comparable.class));
		assertFalse(ClassUtil.isTypeOf(TFooBean.class, Comparable.class));

		assertTrue(ClassUtil.isTypeOf(TFooBean.class, TFooIndyEx.class));
		assertTrue(ClassUtil.isTypeOf(TFooBean2.class, TFooIndyEx.class));
		assertTrue(ClassUtil.isTypeOf(TFooBean.class, TFooIndy.class));
	}

	@Test
	public void testMatchInterfaces() {
		assertTrue(ClassUtil.isTypeOf(HashMap.class, Map.class));
		assertTrue(ClassUtil.isTypeOf(AbstractMap.class, Map.class));
		assertTrue(ClassUtil.isTypeOf(Map.class, Map.class));

		assertTrue(ClassUtil.isInstanceOf(new HashMap(), Map.class));

		assertTrue(ClassUtil.isTypeOf(HashMap.class, Map.class));
		assertTrue(ClassUtil.isTypeOf(AbstractMap.class, Map.class));
		assertTrue(ClassUtil.isTypeOf(HashMap.class, Map.class));
		assertTrue(ClassUtil.isTypeOf(Map.class, Map.class));

		assertTrue(ClassUtil.isTypeOf(HashMap.class, Map.class));
		assertTrue(ClassUtil.isTypeOf(AbstractMap.class, Map.class));
		assertTrue(ClassUtil.isTypeOf(HashMap.class, Map.class));
		assertTrue(ClassUtil.isTypeOf(Map.class, Map.class));
	}


	@Test
	public void testAccessibleA() {
		Method[] ms = ClassUtil.getAccessibleMethods(A.class, null);
		assertEquals(4 + 11, ms.length);            // there are 11 accessible Object methods (9 public + 2 protected)
		ms = ClassUtil.getAccessibleMethods(A.class);
		assertEquals(4, ms.length);
		ms = A.class.getMethods();
		assertEquals(1 + 9, ms.length);                // there are 9 public Object methods
		ms = A.class.getDeclaredMethods();
		assertEquals(4, ms.length);
		ms = ClassUtil.getSupportedMethods(A.class, null);
		assertEquals(4 + 12, ms.length);            // there are 12 total Object methods (9 public + 2 protected + 1 private)
		ms = ClassUtil.getSupportedMethods(A.class);
		assertEquals(4, ms.length);


		Field[] fs = ClassUtil.getAccessibleFields(A.class);
		assertEquals(4, fs.length);
		fs = A.class.getFields();
		assertEquals(1, fs.length);
		fs = A.class.getDeclaredFields();
		assertEquals(4, fs.length);
		fs = ClassUtil.getSupportedFields(A.class);
		assertEquals(4, fs.length);
	}

	@Test
	public void testAccessibleB() {
		Method[] ms = ClassUtil.getAccessibleMethods(B.class, null);
		assertEquals(3 + 11, ms.length);
		ms = ClassUtil.getAccessibleMethods(B.class);
		assertEquals(3, ms.length);
		ms = B.class.getMethods();
		assertEquals(1 + 9, ms.length);
		ms = B.class.getDeclaredMethods();
		assertEquals(0, ms.length);
		ms = ClassUtil.getSupportedMethods(B.class, null);
		assertEquals(4 + 12, ms.length);
		ms = ClassUtil.getSupportedMethods(B.class);
		assertEquals(4, ms.length);


		Field[] fs = ClassUtil.getAccessibleFields(B.class);
		assertEquals(3, fs.length);
		fs = B.class.getFields();
		assertEquals(1, fs.length);
		fs = B.class.getDeclaredFields();
		assertEquals(0, fs.length);
		fs = ClassUtil.getSupportedFields(B.class);
		assertEquals(4, fs.length);
	}

	@Test
	public void testAccessibleC() {
		Method[] ms = ClassUtil.getAccessibleMethods(C.class, null);
		assertEquals(5 + 11, ms.length);
		ms = ClassUtil.getAccessibleMethods(C.class);
		assertEquals(5, ms.length);
		ms = C.class.getMethods();
		assertEquals(2 + 9, ms.length);
		ms = C.class.getDeclaredMethods();
		assertEquals(5, ms.length);
		ms = ClassUtil.getSupportedMethods(C.class, null);
		assertEquals(5 + 12, ms.length);
		ms = ClassUtil.getSupportedMethods(C.class);
		assertEquals(5, ms.length);


		Field[] fs = ClassUtil.getAccessibleFields(C.class);
		assertEquals(5, fs.length);
		fs = C.class.getFields();
		assertEquals(3, fs.length);
		fs = C.class.getDeclaredFields();
		assertEquals(5, fs.length);
		fs = ClassUtil.getSupportedFields(C.class);
		assertEquals(5, fs.length);
	}

	@Test
	public void testAccessibleD() {
		Method[] ms = ClassUtil.getAccessibleMethods(D.class, null);
		assertEquals(3 + 11, ms.length);
		ms = ClassUtil.getAccessibleMethods(D.class);
		assertEquals(3, ms.length);
		ms = D.class.getMethods();
		assertEquals(2 + 9, ms.length);
		ms = D.class.getDeclaredMethods();
		assertEquals(0, ms.length);
		ms = ClassUtil.getSupportedMethods(D.class, null);
		assertEquals(5 + 12, ms.length);
		ms = ClassUtil.getSupportedMethods(D.class);
		assertEquals(5, ms.length);

		Field[] fs = ClassUtil.getAccessibleFields(D.class);
		assertEquals(3, fs.length);
		fs = D.class.getFields();
		assertEquals(3, fs.length);
		fs = D.class.getDeclaredFields();
		assertEquals(0, fs.length);
		fs = ClassUtil.getSupportedFields(D.class);
		assertEquals(5, fs.length);
	}

	@Test
	public void testAccessibleE() {
		Method[] ms = ClassUtil.getAccessibleMethods(E.class, null);
		assertEquals(5 + 11, ms.length);
		ms = ClassUtil.getAccessibleMethods(E.class);
		assertEquals(5, ms.length);
		ms = E.class.getMethods();
		assertEquals(2 + 9, ms.length);
		ms = E.class.getDeclaredMethods();
		assertEquals(4, ms.length);
		ms = ClassUtil.getSupportedMethods(E.class, null);
		assertEquals(5 + 12, ms.length);
		ms = ClassUtil.getSupportedMethods(E.class);
		assertEquals(5, ms.length);

		Field[] fs = ClassUtil.getAccessibleFields(E.class);
		assertEquals(5, fs.length);
		fs = E.class.getFields();
		assertEquals(4, fs.length);
		fs = E.class.getDeclaredFields();
		assertEquals(4, fs.length);
		fs = ClassUtil.getSupportedFields(E.class);
		assertEquals(5, fs.length);
	}


	@Test
	public void testIsSubclassAndInterface() {
		assertTrue(ClassUtil.isTypeOf(SBase.class, SBase.class));

		assertTrue(ClassUtil.isTypeOf(SOne.class, SBase.class));
		assertTrue(ClassUtil.isTypeOf(SOne.class, IOne.class));
		assertTrue(ClassUtil.isTypeOf(SOne.class, IOne.class));
		assertTrue(ClassUtil.isTypeOf(SOne.class, Serializable.class));
		assertTrue(ClassUtil.isTypeOf(SOne.class, Serializable.class));
		assertTrue(ClassUtil.isTypeOf(SOne.class, SOne.class));

		assertTrue(ClassUtil.isTypeOf(STwo.class, SBase.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, IOne.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, IOne.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, Serializable.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, Serializable.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, ITwo.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, ITwo.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, IBase.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, IBase.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, IExtra.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, IExtra.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, STwo.class));
		assertTrue(ClassUtil.isTypeOf(STwo.class, STwo.class));
	}

	@Test
	public void testBeanPropertyNames() {
		String name = ClassUtil.getBeanPropertyGetterName(ClassUtil.findMethod(JavaBean.class, "getOne"));
		assertEquals("one", name);

		name = ClassUtil.getBeanPropertySetterName(ClassUtil.findMethod(JavaBean.class, "setOne"));
		assertEquals("one", name);

		name = ClassUtil.getBeanPropertyGetterName(ClassUtil.findMethod(JavaBean.class, "isTwo"));
		assertEquals("two", name);

		name = ClassUtil.getBeanPropertySetterName(ClassUtil.findMethod(JavaBean.class, "setTwo"));
		assertEquals("two", name);

		name = ClassUtil.getBeanPropertyGetterName(ClassUtil.findMethod(JavaBean.class, "getThree"));
		assertEquals("three", name);

		name = ClassUtil.getBeanPropertySetterName(ClassUtil.findMethod(JavaBean.class, "setThree"));
		assertEquals("three", name);

		name = ClassUtil.getBeanPropertyGetterName(ClassUtil.findMethod(JavaBean.class, "getF"));
		assertEquals("f", name);

		name = ClassUtil.getBeanPropertySetterName(ClassUtil.findMethod(JavaBean.class, "setF"));
		assertEquals("f", name);

		name = ClassUtil.getBeanPropertyGetterName(ClassUtil.findMethod(JavaBean.class, "getG"));
		assertEquals("g", name);

		name = ClassUtil.getBeanPropertySetterName(ClassUtil.findMethod(JavaBean.class, "setG"));
		assertEquals("g", name);

		name = ClassUtil.getBeanPropertyGetterName(ClassUtil.findMethod(JavaBean.class, "getURL"));
		assertEquals("URL", name);

		name = ClassUtil.getBeanPropertySetterName(ClassUtil.findMethod(JavaBean.class, "setURL"));
		assertEquals("URL", name);

		name = ClassUtil.getBeanPropertyGetterName(ClassUtil.findMethod(JavaBean.class, "getBIGsmall"));
		assertEquals("BIGsmall", name);

		name = ClassUtil.getBeanPropertySetterName(ClassUtil.findMethod(JavaBean.class, "setBIGsmall"));
		assertEquals("BIGsmall", name);
	}

	@Test
	public void testIsSubClassForCommonTypes() {
		assertTrue(ClassUtil.isTypeOf(Long.class, Long.class));
		assertFalse(ClassUtil.isTypeOf(Long.class, long.class));
	}

/*	@Test
	public void testGetCallerClass() {
		assertFalse(Reflection.getCallerClass(0).equals(ReflectUtil.getCallerClass(0)));

		assertEquals(Reflection.getCallerClass(1), ReflectUtil.getCallerClass(1));
		assertEquals(Reflection.getCallerClass(2), ReflectUtil.getCallerClass(2));
		assertEquals(Reflection.getCallerClass(3), ReflectUtil.getCallerClass(3));

		assertEquals(ReflectUtilTest.class, ReflectUtil.getCallerClass(1));
	}

	@Test
	public void testGetCallerClass2() throws NoSuchFieldException, IllegalAccessException {
		Field field = ReflectUtil.class.getDeclaredField("SECURITY_MANAGER");
		field.setAccessible(true);
		Object value = field.get(null);
		field.set(null, null);

		assertFalse(Reflection.getCallerClass(0).equals(ReflectUtil.getCallerClass(0)));

		assertEquals(Reflection.getCallerClass(1), ReflectUtil.getCallerClass(1));
		assertEquals(Reflection.getCallerClass(2), ReflectUtil.getCallerClass(2));
		assertEquals(Reflection.getCallerClass(3), ReflectUtil.getCallerClass(3));

		assertEquals(ReflectUtilTest.class, ReflectUtil.getCallerClass(1));

		field.set(null, value);
	}
*/
	// ---------------------------------------------------------------- field concrete type

	public static class BaseClass<A, B> {
		public A f1;
		public B f2;
		public String f3;
		public A[] array1;
	}

	public static class ConcreteClass extends BaseClass<String, Integer> {
		public Long f4;
		public List<Long> f5;
	}

	public static class BaseClass2<X> extends BaseClass<X, Integer> {
	}

	public static class ConcreteClass2 extends BaseClass2<String> {
	}

	@Test
	public void testGetFieldConcreteType() throws NoSuchFieldException {
		Field f1 = BaseClass.class.getField("f1");
		Field f2 = BaseClass.class.getField("f2");
		Field f3 = BaseClass.class.getField("f3");
		Field f4 = ConcreteClass.class.getField("f4");
		Field f5 = ConcreteClass.class.getField("f5");
		Field array1 = BaseClass.class.getField("array1");

		Class[] genericSupertypes = ClassUtil.getGenericSupertypes(ConcreteClass.class);
		assertEquals(String.class, genericSupertypes[0]);
		assertEquals(Integer.class, genericSupertypes[1]);

		assertEquals(String.class, ClassUtil.getRawType(f1.getGenericType(), ConcreteClass.class));
		assertEquals(Integer.class, ClassUtil.getRawType(f2.getGenericType(), ConcreteClass.class));
		assertEquals(String.class, ClassUtil.getRawType(f3.getGenericType(), ConcreteClass.class));
		assertEquals(Long.class, ClassUtil.getRawType(f4.getGenericType(), ConcreteClass.class));
		assertEquals(List.class, ClassUtil.getRawType(f5.getGenericType(), ConcreteClass.class));
		assertEquals(String[].class, ClassUtil.getRawType(array1.getGenericType(), ConcreteClass.class));

		assertEquals(Object.class, ClassUtil.getRawType(f1.getGenericType()));
		assertNull(ClassUtil.getComponentType(f1.getGenericType(), -1));
		assertEquals(Long.class, ClassUtil.getComponentType(f5.getGenericType(), 0));
	}

	@Test
	public void testGetFieldConcreteType2() throws Exception {
		Field array1 = BaseClass.class.getField("array1");
		Field f2 = ConcreteClass2.class.getField("f2");

		assertEquals(String[].class, ClassUtil.getRawType(array1.getGenericType(), ConcreteClass2.class));
		assertEquals(Integer.class, ClassUtil.getRawType(f2.getGenericType(), ConcreteClass2.class));
		assertEquals(Integer.class, ClassUtil.getRawType(f2.getGenericType(), BaseClass2.class));
	}

	// ---------------------------------------------------------------- test raw

	public static class Soo {
		public List<String> stringList;
		public String[] strings;
		public String string;

		public List<Integer> getIntegerList() {return null;}
		public Integer[] getIntegers() {return null;}
		public Integer getInteger() {return null;}
		public <T> T getTemplate(T foo) {return null;}
		public Collection<? extends Number> getCollection() {return null;}
		public Collection<?> getCollection2() {return null;}
	}

	@Test
	public void testGetRawAndComponentType() throws NoSuchFieldException {

		Class<Soo> sooClass = Soo.class;

		Field stringList = sooClass.getField("stringList");
		assertEquals(List.class, ClassUtil.getRawType(stringList.getType()));
		assertEquals(String.class, ClassUtil.getComponentType(stringList.getGenericType(), 0));

		Field strings = sooClass.getField("strings");
		assertEquals(String[].class, ClassUtil.getRawType(strings.getType()));
		assertEquals(String.class, ClassUtil.getComponentType(strings.getGenericType(), -1));

		Field string = sooClass.getField("string");
		assertEquals(String.class, ClassUtil.getRawType(string.getType()));
		assertNull(ClassUtil.getComponentType(string.getGenericType(), 0));

		Method integerList = ClassUtil.findMethod(sooClass, "getIntegerList");
		assertEquals(List.class, ClassUtil.getRawType(integerList.getReturnType()));
		assertEquals(Integer.class, ClassUtil.getComponentType(integerList.getGenericReturnType(), -1));

		Method integers = ClassUtil.findMethod(sooClass, "getIntegers");
		assertEquals(Integer[].class, ClassUtil.getRawType(integers.getReturnType()));
		assertEquals(Integer.class, ClassUtil.getComponentType(integers.getGenericReturnType(), 0));

		Method integer = ClassUtil.findMethod(sooClass, "getInteger");
		assertEquals(Integer.class, ClassUtil.getRawType(integer.getReturnType()));
		assertNull(ClassUtil.getComponentType(integer.getGenericReturnType(), -1));

		Method template = ClassUtil.findMethod(sooClass, "getTemplate");
		assertEquals(Object.class, ClassUtil.getRawType(template.getReturnType()));
		assertNull(ClassUtil.getComponentType(template.getGenericReturnType(), 0));

		Method collection = ClassUtil.findMethod(sooClass, "getCollection");
		assertEquals(Collection.class, ClassUtil.getRawType(collection.getReturnType()));
		assertEquals(Number.class, ClassUtil.getComponentType(collection.getGenericReturnType(), -1));

		Method collection2 = ClassUtil.findMethod(sooClass, "getCollection2");
		assertEquals(Collection.class, ClassUtil.getRawType(collection2.getReturnType()));
		assertEquals(Object.class, ClassUtil.getComponentType(collection2.getGenericReturnType(), 0));
	}

	public static class Base2<N extends Number, K> {
		public N getNumber() {return null;}
		public K getKiko() {return null;}
	}
	public static class Impl1<N extends Number> extends Base2<N, Long> {}
	public static class Impl2 extends Impl1<Integer> {}
	public static class Impl3 extends Impl2 {}

	@Test
	public void testGetRawWithImplClass() throws NoSuchFieldException {
		Method number = ClassUtil.findMethod(Base2.class, "getNumber");
		Method kiko = ClassUtil.findMethod(Base2.class, "getKiko");

		assertEquals(Number.class, ClassUtil.getRawType(number.getReturnType()));
		assertEquals(Number.class, ClassUtil.getRawType(number.getGenericReturnType()));

		assertEquals(Object.class, ClassUtil.getRawType(kiko.getReturnType()));
		assertEquals(Object.class, ClassUtil.getRawType(kiko.getGenericReturnType()));

		assertEquals(Number.class, ClassUtil.getRawType(number.getReturnType(), Impl1.class));
		assertEquals(Number.class, ClassUtil.getRawType(number.getGenericReturnType(), Impl1.class));

		assertEquals(Object.class, ClassUtil.getRawType(kiko.getReturnType(), Impl1.class));
		assertEquals(Long.class, ClassUtil.getRawType(kiko.getGenericReturnType(), Impl1.class));

		assertEquals(Number.class, ClassUtil.getRawType(number.getReturnType(), Impl2.class));
		assertEquals(Integer.class, ClassUtil.getRawType(number.getGenericReturnType(), Impl2.class));

		assertEquals(Object.class, ClassUtil.getRawType(kiko.getReturnType(), Impl2.class));
		assertEquals(Long.class, ClassUtil.getRawType(kiko.getGenericReturnType(), Impl2.class));

		assertEquals(Number.class, ClassUtil.getRawType(number.getReturnType(), Impl3.class));
		assertEquals(Integer.class, ClassUtil.getRawType(number.getGenericReturnType(), Impl3.class));

		assertEquals(Object.class, ClassUtil.getRawType(kiko.getReturnType(), Impl3.class));
		assertEquals(Long.class, ClassUtil.getRawType(kiko.getGenericReturnType(), Impl3.class));
	}

	public static class Base22<K, N extends Number> {}
	public static class Impl11<N extends Number> extends Base22<Long, N> {}
	public static class Impl22 extends Impl11<Integer> {}
	public static class Impl33 extends Impl22 {}


	@Test
	public void testClassGenerics1() {
		Class[] componentTypes = ClassUtil.getGenericSupertypes(Base2.class);
		assertNull(componentTypes);

		Type[] types = Base2.class.getGenericInterfaces();
		assertEquals(0, types.length);

		componentTypes = ClassUtil.getGenericSupertypes(Impl1.class);
		assertEquals(2, componentTypes.length);
		assertEquals(Number.class, componentTypes[0]);
		assertEquals(Long.class, componentTypes[1]);

		types = Impl1.class.getGenericInterfaces();
		assertEquals(0, types.length);

		componentTypes = ClassUtil.getGenericSupertypes(Impl2.class);
		assertEquals(1, componentTypes.length);
		assertEquals(Integer.class, componentTypes[0]);

		types = Impl2.class.getGenericInterfaces();
		assertEquals(0, types.length);

		componentTypes = ClassUtil.getGenericSupertypes(Impl3.class);
		assertNull(componentTypes);
	}

	@Test
	public void testClassGenerics2() {
		Class[] componentTypes = ClassUtil.getGenericSupertypes(Base22.class);
		assertNull(componentTypes);

		componentTypes = ClassUtil.getGenericSupertypes(Impl11.class);
		assertEquals(2, componentTypes.length);
		assertEquals(Long.class, componentTypes[0]);
		assertEquals(Number.class, componentTypes[1]);

		componentTypes = ClassUtil.getGenericSupertypes(Impl22.class);
		assertEquals(1, componentTypes.length);
		assertEquals(Integer.class, componentTypes[0]);

		componentTypes = ClassUtil.getGenericSupertypes(Impl33.class);
		assertNull(componentTypes);
	}

	public static interface BaseAna<K, N extends Number> {}
	public static interface ImplAna<N extends Number> extends BaseAna<Long, N> {}
	public static interface ImplAna2 extends ImplAna<Integer> {}
	public static class ImplAna3 implements ImplAna2 {}
	public static class ImplAna4 extends ImplAna3 {}

	@Test
	public void testClassGenerics3() {
		Class[] componentTypes = ClassUtil.getGenericSupertypes(BaseAna.class);
		assertNull(componentTypes);

		componentTypes = ClassUtil.getGenericSupertypes(ImplAna.class);
		assertNull(componentTypes);

		componentTypes = ClassUtil.getGenericSupertypes(ImplAna2.class);
		assertNull(componentTypes);

		componentTypes = ClassUtil.getGenericSupertypes(ImplAna3.class);
		assertNull(componentTypes);

		// scan generic interfacase

		Type[] types = ImplAna3.class.getGenericInterfaces();
		assertEquals(1, types.length);
		assertEquals(ImplAna2.class, types[0]);
		assertNull(ClassUtil.getComponentType(types[0], 0));

		types = ImplAna2.class.getGenericInterfaces();
		assertEquals(1, types.length);
		assertEquals(Integer.class, ClassUtil.getComponentType(types[0], 0));

		types = ImplAna.class.getGenericInterfaces();
		assertEquals(1, types.length);
		assertEquals(Long.class, ClassUtil.getComponentType(types[0], 0));

		types = BaseAna.class.getGenericInterfaces();
		assertEquals(0, types.length);

		types = ImplAna4.class.getGenericInterfaces();
		assertEquals(0, types.length);
	}


	// ---------------------------------------------------------------- type2string

	public static class FieldType<K extends Number, V extends List<String> & Collection<String>> {
		List fRaw;
		List<Object> fTypeObject;
		List<String> fTypeString;
		List<?> fWildcard;
		List<? super List<String>> fBoundedWildcard;
		Map<String, List<Set<Long>>> fTypeNested;
		Map<K, V> fTypeLiteral;
		K[] fGenericArray;
	}

	@Test
	public void testFieldTypeToString() {
		Field[] fields = FieldType.class.getDeclaredFields();

		Arrays.sort(fields, new Comparator<Field>() {
			public int compare(Field o1, Field o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		String result = "";
		for (Field field : fields) {
			Type type = field.getGenericType();
			result += field.getName() + " - " + ClassUtil.typeToString(type) + '\n';
		}

		assertEquals(
				"fBoundedWildcard - java.util.List<? super java.util.List<java.lang.String>>\n" +
				"fGenericArray - K[]\n" +
				"fRaw - java.util.List\n" +
				"fTypeLiteral - java.util.Map<K extends java.lang.Number>, <V extends java.util.List<java.lang.String> & java.util.Collection<java.lang.String>>\n" +
				"fTypeNested - java.util.Map<java.lang.String>, <java.util.List<java.util.Set<java.lang.Long>>>\n" +
				"fTypeObject - java.util.List<java.lang.Object>\n" +
				"fTypeString - java.util.List<java.lang.String>\n" +
				"fWildcard - java.util.List<? extends java.lang.Object>\n",
				result);
	}

	public static class MethodReturnType {
		List mRaw() {return null;}
		List<String> mTypeString() {return null;}
		List<?> mWildcard() {return null;}
		List<? extends Number> mBoundedWildcard() {return null;}
		<T extends List<String>> List<T> mTypeLiteral() {return null;}
	}

	@Test
	public void testMethodTypeToString() {
		Method[] methods = MethodReturnType.class.getDeclaredMethods();

		Arrays.sort(methods, new Comparator<Method>() {
			public int compare(Method o1, Method o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		String result = "";
		for (Method method : methods) {
			Type type = method.getGenericReturnType();
			result += method.getName() + " - " + ClassUtil.typeToString(type) + '\n';
		}

		assertEquals(
				"mBoundedWildcard - java.util.List<? extends java.lang.Number>\n" +
				"mRaw - java.util.List\n" +
				"mTypeLiteral - java.util.List<T extends java.util.List<java.lang.String>>\n" +
				"mTypeString - java.util.List<java.lang.String>\n" +
				"mWildcard - java.util.List<? extends java.lang.Object>\n",
				result);
	}

	public static class MethodParameterType<A> {
		<T extends List<T>> void m(A a, String p1, T p2, List<?> p3, List<T> p4) { }
	}

	public static class Mimple extends MethodParameterType<Long>{}

	@Test
	public void testMethodParameterTypeToString() {
		String result = "";
		Method method = null;
		for (Method m : MethodParameterType.class.getDeclaredMethods()) {
			for (Type type : m.getGenericParameterTypes()) {
				result += m.getName() + " - " + ClassUtil.typeToString(type) + '\n';
			}
			method = m;
		}

		assertEquals(
				"m - A extends java.lang.Object\n" +
				"m - java.lang.String\n" +
				"m - T extends java.util.List<T>\n" +
				"m - java.util.List<? extends java.lang.Object>\n" +
				"m - java.util.List<T extends java.util.List<T>>\n",
				result);


		Type[] types = method.getGenericParameterTypes();
		assertEquals(Object.class, ClassUtil.getRawType(types[0], MethodParameterType.class));
		assertEquals(String.class, ClassUtil.getRawType(types[1], MethodParameterType.class));
		assertEquals(List.class, ClassUtil.getRawType(types[2], MethodParameterType.class));
		assertEquals(List.class, ClassUtil.getRawType(types[3], MethodParameterType.class));
		assertEquals(List.class, ClassUtil.getRawType(types[4], MethodParameterType.class));

		// same methods, using different impl class
		assertEquals(Long.class, ClassUtil.getRawType(types[0], Mimple.class));		// change!
		assertEquals(String.class, ClassUtil.getRawType(types[1], Mimple.class));
		assertEquals(List.class, ClassUtil.getRawType(types[2], Mimple.class));
		assertEquals(List.class, ClassUtil.getRawType(types[3], Mimple.class));
		assertEquals(List.class, ClassUtil.getRawType(types[4], Mimple.class));
	}

	public interface SomeGuy {}
	public interface Cool extends SomeGuy {}
	public interface Vigilante {}
	public interface Flying extends Vigilante {}
	public interface SuperMario extends Flying, Cool {}
	public class User implements SomeGuy {}
	public class SuperUser extends User implements Cool {}
	public class SuperMan extends SuperUser implements Flying {}

	@Test
	public void testResolveAllInterfaces() {
		Class[] interfaces = ClassUtil.resolveAllInterfaces(HashMap.class);

		assertTrue(interfaces.length >= 3);
		assertTrue(ArraysUtil.contains(interfaces, Map.class));
		assertTrue(ArraysUtil.contains(interfaces, Serializable.class));
		assertTrue(ArraysUtil.contains(interfaces, Cloneable.class));

		interfaces = ClassUtil.resolveAllInterfaces(SuperMan.class);

		assertEquals(4, interfaces.length);
		assertTrue(ArraysUtil.contains(interfaces, SomeGuy.class));
		assertTrue(ArraysUtil.contains(interfaces, Cool.class));
		assertTrue(ArraysUtil.contains(interfaces, Flying.class));
		assertTrue(ArraysUtil.contains(interfaces, Vigilante.class));
		assertTrue(ArraysUtil.indexOf(interfaces, Flying.class) < ArraysUtil.indexOf(interfaces, SomeGuy.class));

		interfaces = ClassUtil.resolveAllInterfaces(SuperUser.class);

		assertEquals(2, interfaces.length);
		assertTrue(ArraysUtil.contains(interfaces, SomeGuy.class));
		assertTrue(ArraysUtil.contains(interfaces, Cool.class));

		interfaces = ClassUtil.resolveAllInterfaces(User.class);

		assertEquals(1, interfaces.length);
		assertTrue(ArraysUtil.contains(interfaces, SomeGuy.class));



		interfaces = ClassUtil.resolveAllInterfaces(SomeGuy.class);
		assertEquals(0, interfaces.length);

		interfaces = ClassUtil.resolveAllInterfaces(Cool.class);
		assertEquals(1, interfaces.length);

		interfaces = ClassUtil.resolveAllInterfaces(Vigilante.class);
		assertEquals(0, interfaces.length);

		interfaces = ClassUtil.resolveAllInterfaces(Flying.class);
		assertEquals(1, interfaces.length);

		interfaces = ClassUtil.resolveAllInterfaces(SuperMario.class);
		assertEquals(4, interfaces.length);



		interfaces = ClassUtil.resolveAllInterfaces(Object.class);
		assertEquals(0, interfaces.length);
		interfaces = ClassUtil.resolveAllInterfaces(int.class);
		assertEquals(0, interfaces.length);
		interfaces = ClassUtil.resolveAllInterfaces(int[].class);
		assertEquals(2, interfaces.length);		// cloneable, serializable
		interfaces = ClassUtil.resolveAllInterfaces(Integer[].class);
		assertEquals(2, interfaces.length);
	}

	@Test
	public void testResolveAllSuperclsses() {
		Class[] subclasses = ClassUtil.resolveAllSuperclasses(User.class);
		assertEquals(0, subclasses.length);

		subclasses = ClassUtil.resolveAllSuperclasses(SuperUser.class);
		assertEquals(1, subclasses.length);
		assertEquals(User.class, subclasses[0]);

		subclasses = ClassUtil.resolveAllSuperclasses(SuperMan.class);
		assertEquals(2, subclasses.length);
		assertEquals(SuperUser.class, subclasses[0]);
		assertEquals(User.class, subclasses[1]);


		subclasses = ClassUtil.resolveAllSuperclasses(Cool.class);
		assertEquals(0, subclasses.length);
		subclasses = ClassUtil.resolveAllSuperclasses(Flying.class);
		assertEquals(0, subclasses.length);
		subclasses = ClassUtil.resolveAllSuperclasses(SuperMario.class);
		assertEquals(0, subclasses.length);


		subclasses = ClassUtil.resolveAllSuperclasses(Object.class);
		assertEquals(0, subclasses.length);
		subclasses = ClassUtil.resolveAllSuperclasses(int.class);
		assertEquals(0, subclasses.length);
		subclasses = ClassUtil.resolveAllSuperclasses(int[].class);
		assertEquals(0, subclasses.length);
		subclasses = ClassUtil.resolveAllSuperclasses(Integer[].class);
		assertEquals(0, subclasses.length);
	}
}
