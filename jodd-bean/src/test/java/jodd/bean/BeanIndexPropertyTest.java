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

package jodd.bean;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class BeanIndexPropertyTest {

	// simple

	public static class Simple<T extends Number> {
		public T data;

		public T getDataX() {return data;}
		public void setDataX(T data) {this.data = data;}
	}
	public static class SimpleLong extends Simple<Long> {}

	// bean 1

	public static class Bean1 {
		public Simple[] array1;
		public SimpleLong[] array2;
		public List<Simple> list1;
		public Map<String, Simple> map1;
		public Map<Integer, Simple> map2;

		public Simple[] getArray1X() {return array1;}
		public void setArray1X(Simple[] array1) {this.array1 = array1;}

		public SimpleLong[] getArray2X() {return array2;}
		public void setArray2X(SimpleLong[] array2) {this.array2 = array2;}

		public List<Simple> getList1X() {return list1;}
		public void setList1X(List<Simple> list1) {this.list1 = list1;}

		public Map<String, Simple> getMap1X() {return map1;}
		public void setMap1X(Map<String, Simple> map1) {this.map1 = map1;}

		public Map<Integer, Simple> getMap2X() {return map2;}
		public void setMap2X(Map<Integer, Simple> map2) {this.map2 = map2;}
	}

	public static class Bean2<T extends Number> {
		public T[] array1;
		public List<T> list1;
		public Map<String, T> map1;
		public Map<Integer, T> map2;

		public T[] getArray1X() {return array1;}
		public void setArray1X(T[] array1) {this.array1 = array1;}

		public List<T> getList1X() {return list1;}
		public void setList1X(List<T> list1) {this.list1 = list1;}

		public Map<String, T> getMap1X() {return map1;}
		public void setMap1X(Map<String, T> map1) {this.map1 = map1;}

		public Map<Integer, T> getMap2X() {return map2;}
		public void setMap2X(Map<Integer, T> map2) {this.map2 = map2;}
	}
	public static class Bean2Long extends Bean2<Long> {}


	// ---------------------------------------------------------------- tests

	@Test
	public void testGetSetArray1() {
		for (int i = 0; i < 2; i++) {
			String suffix = "";
			if (i == 1) {
				suffix = "X";
			}

			Bean1 bean1 = new Bean1();

			assertNull(BeanUtil.pojo.getProperty(bean1, "array1" + suffix));
			try {
				BeanUtil.pojo.getProperty(bean1, "array1" + suffix + "[0]");
				fail("error");	// fails to read index of null property
			} catch (BeanException ignore) {
			}

			// array1[0]

			try {
				BeanUtil.pojo.setProperty(bean1, "array1" + suffix + "[0].data" + suffix, Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignored) {
			}

			BeanUtil.forced.setProperty(bean1, "array1" + suffix + "[0].data" + suffix, Integer.valueOf(173));
			assertNotNull(bean1.array1);
			assertEquals(Integer.valueOf(173), bean1.array1[0].data);

			// array2[0]

			try {
				BeanUtil.pojo.setProperty(bean1, "array2" + suffix + "[0].data" + suffix, Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignored) {
			}

			BeanUtil.forced.setProperty(bean1, "array2" + suffix + "[0].data" + suffix, Integer.valueOf(173));
			assertNotNull(bean1.array2);
			assertEquals(Long.valueOf(173), bean1.array2[0].data);
		}

	}

	@Test
	public void testGetSetArray2() {
		for (int i = 0; i < 2; i++) {
			String suffix = "";
			if (i == 1) {
				suffix = "X";
			}

			Bean2 bean2 = new Bean2();
			Bean2Long bean2Long = new Bean2Long();

			// array1[0]

			try {
				BeanUtil.pojo.setProperty(bean2, "array1" + suffix + "[0]", Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignore) {
			}

			BeanUtil.forced.setProperty(bean2, "array1" + suffix + "[0]", Integer.valueOf(173));
			assertNotNull(bean2.array1);
			assertEquals(Integer.valueOf(173), bean2.array1[0]);

			try {
				BeanUtil.pojo.setProperty(bean2Long, "array1" + suffix + "[0]", Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignore) {
			}

			BeanUtil.forced.setProperty(bean2Long, "array1" + suffix + "[0]", Integer.valueOf(173));
			assertNotNull(bean2Long.array1);
			assertEquals(Long.valueOf(173), bean2Long.array1[0]);
			assertEquals(Long.class, bean2Long.array1[0].getClass());
		}
	}

	// ---------------------------------------------------------------- list

	@Test
	public void testGetSetList() {
		for (int i = 0; i < 2; i++) {
			String suffix = "";
			if (i == 1) {
				suffix = "X";
			}

			Bean1 bean1 = new Bean1();

			assertNull(BeanUtil.pojo.getProperty(bean1, "list1" + suffix));
			try {
				BeanUtil.pojo.getProperty(bean1, "list1" + suffix + "[0]");
				fail("error");	// fails to read index of null property
			} catch (BeanException ignored) {
			}

			try {
				BeanUtil.pojo.setProperty(bean1, "list1" + suffix + "[0].data" + suffix, Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignored) {
			}

			BeanUtil.forced.setProperty(bean1, "list1" + suffix + "[0].data" + suffix, Integer.valueOf(173));
			assertNotNull(bean1.list1);
			assertEquals(Integer.valueOf(173), bean1.list1.get(0).data);
		}
	}

	@Test
	public void testGetSetList2() {
		for (int i = 0; i < 2; i++) {
			String suffix = "";
			if (i == 1) {
				suffix = "X";
			}

			Bean2 bean2 = new Bean2();
			Bean2Long bean2Long = new Bean2Long();

			// array1[0]

			try {
				BeanUtil.pojo.setProperty(bean2, "list1" + suffix + "[0]", Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignore) {
			}

			BeanUtil.forced.setProperty(bean2, "list1" + suffix + "[0]", Integer.valueOf(173));
			assertNotNull(bean2.list1);
			assertEquals(Integer.valueOf(173), bean2.list1.get(0));

			try {
				BeanUtil.pojo.setProperty(bean2Long, "list1" + suffix + "[0]", Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignore) {
			}

			BeanUtil.forced.setProperty(bean2Long, "list1" + suffix + "[0]", Integer.valueOf(173));
			assertNotNull(bean2Long.list1);
			assertEquals(Long.valueOf(173), bean2Long.list1.get(0));
			assertEquals(Long.class, bean2Long.list1.get(0).getClass());
		}
	}

	// ---------------------------------------------------------------- map

	@Test
	public void testGetSetMap() {
		for (int i = 0; i < 2; i++) {
			String suffix = "";
			if (i == 1) {
				suffix = "X";
			}

			Bean1 bean1 = new Bean1();

			assertNull(BeanUtil.pojo.getProperty(bean1, "map1" + suffix));

			try {
				BeanUtil.pojo.setProperty(bean1, "map1" + suffix + "[x0].data" + suffix, Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignored) {
			}

			BeanUtil.forced.setProperty(bean1, "map1" + suffix + "[x0].data" + suffix, Integer.valueOf(173));
			assertNotNull(bean1.map1);
			assertEquals(Integer.valueOf(173), bean1.map1.get("x0").data);
		}
	}

	@Test
	public void testGetSetMapWithIntegerKey() {
		for (int i = 0; i < 2; i++) {
			String suffix = "";
			if (i == 1) {
				suffix = "X";
			}

			Bean1 bean1 = new Bean1();

			assertNull(BeanUtil.pojo.getProperty(bean1, "map2" + suffix));

			try {
				BeanUtil.pojo.setProperty(bean1, "map2" + suffix + "[0].data" + suffix, Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignored) {
			}

			BeanUtil.forced.setProperty(bean1, "map2" + suffix + "[0].data" + suffix, Integer.valueOf(173));
			assertNotNull(bean1.map2);
			assertEquals(Integer.valueOf(173), bean1.map2.get(Integer.valueOf(0)).data);
		}
	}

	@Test
	public void testGetSetMap2() {
		for (int i = 0; i < 2; i++) {
			String suffix = "";
			if (i == 1) {
				suffix = "X";
			}

			Bean2 bean2 = new Bean2();
			Bean2Long bean2Long = new Bean2Long();

			// array1[0]

			try {
				BeanUtil.pojo.setProperty(bean2, "map1" + suffix + "[0x]", Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignore) {
			}

			BeanUtil.forced.setProperty(bean2, "map1" + suffix + "[0x]", Integer.valueOf(173));
			assertNotNull(bean2.map1);
			assertEquals(Integer.valueOf(173), bean2.map1.get("0x"));

			try {
				BeanUtil.pojo.setProperty(bean2Long, "map1" + suffix + "[0x]", Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignore) {
			}

			BeanUtil.forced.setProperty(bean2Long, "map1" + suffix + "[0x]", Integer.valueOf(173));
			assertNotNull(bean2Long.map1);
			assertEquals(Long.valueOf(173), bean2Long.map1.get("0x"));
			assertEquals(Long.class, bean2Long.map1.get("0x").getClass());
		}
	}

	@Test
	public void testGetSetMap2withIntegerKey() {
		for (int i = 0; i < 2; i++) {
			String suffix = "";
			if (i == 1) {
				suffix = "X";
			}

			Bean2 bean2 = new Bean2();
			Bean2Long bean2Long = new Bean2Long();

			// array1[0]

			try {
				BeanUtil.pojo.setProperty(bean2, "map2" + suffix + "[0]", Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignore) {
			}

			BeanUtil.forced.setProperty(bean2, "map2" + suffix + "[0]", Integer.valueOf(173));
			assertNotNull(bean2.map2);
			assertEquals(Integer.valueOf(173), bean2.map2.get(Integer.valueOf(0)));

			try {
				BeanUtil.pojo.setProperty(bean2Long, "map2" + suffix + "[0]", Integer.valueOf(173));
				fail("error");
			} catch (BeanException ignore) {
			}

			BeanUtil.forced.setProperty(bean2Long, "map2" + suffix + "[0]", Integer.valueOf(173));
			assertNotNull(bean2Long.map2);
			assertEquals(Long.valueOf(173), bean2Long.map2.get(Integer.valueOf(0)));
			assertEquals(Long.class, bean2Long.map2.get(Integer.valueOf(0)).getClass());
		}
	}

}
