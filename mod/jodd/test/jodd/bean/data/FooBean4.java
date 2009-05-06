// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FooBean4 {

	public FooBean4() {
		data = new Cbean[3];
		data[0] = new Cbean();
		data[0].getBbean().getAbean().setFooProp("xxx");
		data[1] = new Cbean();
		data[1].getBbean().getAbean().setFooProp("yyy");
		data[2] = new Cbean();
		data[2].getBbean().getAbean().setFooProp("zzz");
		list = new ArrayList();
		Cbean tmp = new Cbean();
		tmp.getBbean().getAbean().setFooProp("LLL");
		list.add(tmp);
		list.add("lll");
	}

	private Cbean[] data;

	public Cbean[] getData() {
		return data;
	}

	public void setData(Cbean[] data) {
		this.data = data;
	}


	private FooBean[] foo = new FooBean[5];	// must!!!
	public FooBean[] getFoo() {
		return foo;
	}
	public void setFoo(FooBean[] foo) {
		this.foo = foo;
	}

	private ArrayList list;

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	private HashMap map;
	public Map getMap() {
		return map;
	}

	public void setMap(HashMap map) {
		this.map = map;
	}
}
