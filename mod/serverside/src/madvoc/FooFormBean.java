// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.mutable.MutableInteger;
import jodd.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FooFormBean {

	public String check;
	public boolean check1;
	public Boolean[] check2;
	public MutableInteger[] check3 = new MutableInteger[3];
	public String hidden;
	public String text;
	public int text1;
	public Long text2;
	public String textarea;
	public String password;
	public String radio;
	public String select;
	public String[] sarr;
	public List<String> slist;
	public Map<String, String> smap;
	public int[] iarr;

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("\ncheck = ").append(check);
		result.append("\ncheck1 = ").append(check1);
		result.append("\ncheck2 = ").append(Arrays.toString(check2));
		result.append("\ncheck3 = ").append(Arrays.toString(check3));
		result.append("\nhidden = ").append(hidden);
		result.append("\nsarr = ").append(Arrays.toString(sarr));
		result.append("\npassword = ").append(password);
		result.append("\nradio = ").append(radio);
		result.append("\nselect = ").append(select);
		result.append("\ntext = ").append(text);
		result.append("\ntext1 = ").append(text1);
		result.append("\ntext2 = ").append(text2);
		result.append("\ntextarea = ").append(textarea);
		result.append("\nslist = ").append(slist);
		result.append("\nsmap = ").append(smap);
		result.append("\niarr = ").append(Arrays.toString(iarr));
		return result.toString();
	}

	// ---------------------------------------------------------------- accessors (not required)

	public void setTextarea(String value) {
		textarea = StringUtil.trimDown(value);
	}


}
