// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

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
	public int[] check4;
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
		result.append("\ncheck4 = ").append(Arrays.toString(check4));
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


	// ---------------------------------------------------------------- getters

	// Getters are necessary for using FooFormBean in JSP!

	public String getCheck() {
		return check;
	}

	public boolean isCheck1() {
		return check1;
	}

	public Boolean[] getCheck2() {
		return check2;
	}

	public MutableInteger[] getCheck3() {
		return check3;
	}

	public int[] getCheck4() {
		return check4;
	}

	public String getHidden() {
		return hidden;
	}

	public String getText() {
		return text;
	}

	public int getText1() {
		return text1;
	}

	public Long getText2() {
		return text2;
	}

	public String getTextarea() {
		return textarea;
	}

	public String getPassword() {
		return password;
	}

	public String getRadio() {
		return radio;
	}

	public String getSelect() {
		return select;
	}

	public String[] getSarr() {
		return sarr;
	}

	public List<String> getSlist() {
		return slist;
	}

	public Map<String, String> getSmap() {
		return smap;
	}

	public int[] getIarr() {
		return iarr;
	}
}
