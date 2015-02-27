// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.bean.BeanUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class SilentTest {

	public static class Bean {
		protected Data data = new Data();
		protected Data data2 = null;
		protected Data[] datas = new Data[] {data, null, data};
		protected Map<String, Number> map = new HashMap<String, Number>();
		protected List<Number> numbers = new ArrayList<Number>();

		private Number wee;

		private Number[] someNumbers = new Number[Integer.valueOf(123)];

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public Data getData2() {
			return data2;
		}

		public void setData2(Data data2) {
			this.data2 = data2;
		}

		public Data[] getDatas() {
			System.out.println(datas[1].getName());
			return datas;
		}

		public void setDatas(Data[] datas) {
			System.out.println(this.datas[1].getName());
			this.datas = datas;
		}

		public Map<String, Number> getMap() {
			return map;
		}

		public void setMap(Map<String, Number> map) {
			this.map = map;
		}

		public List<Number> getNumbers() {
			return numbers;
		}

		public void setNumbers(List<Number> numbers) {
			this.numbers = numbers;
		}

		public String[] getStrings() {
			return new String[0];
		}
	}

	public static class Data {
		protected String name = "Jenny Doe";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Test
	public void testSetPropertySilent() {
		Bean bean = new Bean();

		try {
			BeanUtil.setPropertySilent(bean, "miss", "xxx");

			BeanUtil.setPropertySilent(bean, "miss.miss", "xxx");

			BeanUtil.setPropertySilent(bean, "miss[]", "xxx");
			BeanUtil.setPropertySilent(bean, "miss[1]", "xxx");
			BeanUtil.setPropertySilent(bean, "miss[a]", "xxx");

			BeanUtil.setPropertySilent(bean, "data.miss", "xxx");

			BeanUtil.setPropertySilent(bean, "data[].miss", "xxx");
			BeanUtil.setPropertySilent(bean, "data[1].miss", "xxx");
			BeanUtil.setPropertySilent(bean, "data[1]", "xxx");
			BeanUtil.setPropertySilent(bean, "data[a].miss", "xxx");

			BeanUtil.setPropertySilent(bean, "data2.value", "xxx");
			BeanUtil.setPropertySilent(bean, "data2[1].value", "xxx");

			BeanUtil.setPropertySilent(bean, "data3.value", "xxx");

			BeanUtil.setPropertySilent(bean, "datas", "xxx");

		}
		catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testGetPropertySilent() {
		Bean bean = new Bean();

		assertNull(BeanUtil.getPropertySilently(bean, "miss"));

		assertNull(BeanUtil.getPropertySilently(bean, "datas[1]"));

		assertNull(BeanUtil.getPropertySilently(bean, "datas[a]"));

		assertNull(BeanUtil.getPropertySilently(bean.getMap(), "miss"));
	}

	@Test
	public void testSetPropertyForcedSilent() {
		Bean bean = new Bean();

		try {
			BeanUtil.setPropertyForcedSilent(bean, "numbers[1].xxx", "173");

			BeanUtil.setPropertyForcedSilent(bean, "map[aaa].xxx", "173");

			BeanUtil.setDeclaredPropertyForcedSilent(bean, "someNumbers[3].foo", "173");

			BeanUtil.setDeclaredPropertyForcedSilent(bean, "wee.foo", "173");

			//BeanUtil.setDeclaredPropertyForcedSilent(bean, "someNumbers[mmm].foo", "173");	// no fast silent
			//BeanUtil.setPropertyForcedSilent(bean, "strings[1]", "moo");		// no fast silent
		}
		catch (Exception ex) {
			fail(ex.getMessage());
		}
	}
}