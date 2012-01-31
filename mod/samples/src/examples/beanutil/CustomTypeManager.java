// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.beanutil;

import jodd.bean.BeanUtilBean;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;

public class CustomTypeManager {
	
	static class Bean {
		public Integer value;
	}

	public static void main(String[] args) {
		TypeConverterManagerBean tcmb = new TypeConverterManagerBean();
		tcmb.register(Integer.class, new TypeConverter<Integer>() {
			public Integer convert(Object value) {
				return Integer.valueOf(73);
			}
		});


		BeanUtilBean bub = new BeanUtilBean();
		bub.setTypeConverterManager(tcmb);
		
		
		Bean bean = new Bean();
		
		bub.setProperty(bean, "value", "-12");

		System.out.println(bean.value);
	}
}
