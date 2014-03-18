// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

import java.util.List;

public class MixBean {

	public List<Integer> data;

	// ----------------------------------------------------------------

	private List<Integer> data1;

	public List<Integer> getData2() {
		return data1;
	}

	public void setData2(List<Integer> data1) {
		this.data1 = data1;
	}

	// ----------------------------------------------------------------

	private List<Integer> data5;

	public List<Integer> getData5() {
		return data5;
	}

	public void setData5(List<Integer> data5) {
		this.data5 = data5;
	}
}