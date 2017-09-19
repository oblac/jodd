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

package jodd.typeconverter;

import jodd.bean.BeanUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class SilentTest {

	public static class Bean {
		protected Data data = new Data();
		protected Data data2 = null;
		protected Data[] datas = new Data[] {data, null, data};
		protected Map<String, Number> map = new HashMap<>();
		protected List<Number> numbers = new ArrayList<>();

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
			BeanUtil.silent.setProperty(bean, "miss", "xxx");

			BeanUtil.silent.setProperty(bean, "miss.miss", "xxx");

			BeanUtil.silent.setProperty(bean, "miss[]", "xxx");
			BeanUtil.silent.setProperty(bean, "miss[1]", "xxx");
			BeanUtil.silent.setProperty(bean, "miss[a]", "xxx");

			BeanUtil.silent.setProperty(bean, "data.miss", "xxx");

			BeanUtil.silent.setProperty(bean, "data[].miss", "xxx");
			BeanUtil.silent.setProperty(bean, "data[1].miss", "xxx");
			BeanUtil.silent.setProperty(bean, "data[1]", "xxx");
			BeanUtil.silent.setProperty(bean, "data[a].miss", "xxx");

			BeanUtil.silent.setProperty(bean, "data2.value", "xxx");
			BeanUtil.silent.setProperty(bean, "data2[1].value", "xxx");

			BeanUtil.silent.setProperty(bean, "data3.value", "xxx");

			BeanUtil.silent.setProperty(bean, "datas", "xxx");

		}
		catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testGetPropertySilent() {
		Bean bean = new Bean();

		assertNull(BeanUtil.silent.getProperty(bean, "miss"));

		assertNull(BeanUtil.silent.getProperty(bean, "datas[1]"));

		assertNull(BeanUtil.silent.getProperty(bean, "datas[a]"));

		assertNull(BeanUtil.silent.getProperty(bean.getMap(), "miss"));
	}

	@Test
	public void testSetPropertyForcedSilent() {
		Bean bean = new Bean();

		try {
			BeanUtil.forcedSilent.setProperty(bean, "numbers[1].xxx", "173");

			BeanUtil.forcedSilent.setProperty(bean, "map[aaa].xxx", "173");

			BeanUtil.declaredForcedSilent.setProperty(bean, "someNumbers[3].foo", "173");

			BeanUtil.declaredForcedSilent.setProperty(bean, "wee.foo", "173");

			//BeanUtil.setDeclaredPropertyForcedSilent(bean, "someNumbers[mmm].foo", "173");	// no fast silent
			//BeanUtil.setPropertyForcedSilent(bean, "strings[1]", "moo");		// no fast silent
		}
		catch (Exception ex) {
			fail(ex.getMessage());
		}
	}
}
