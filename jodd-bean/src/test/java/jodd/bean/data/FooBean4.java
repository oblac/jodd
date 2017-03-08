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
