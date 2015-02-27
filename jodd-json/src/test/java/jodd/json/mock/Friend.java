// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Friend {

	private List<String> nicknames = new ArrayList<String>();

	public Friend(String... nicknames) {
		setNicknamesAsArray(nicknames);
	}

	public void setNicknames(List<String> nicknames) {
		this.nicknames = new CopyOnWriteArrayList<String>(nicknames);
	}

	public void setNicknamesAsArray(String[] array) {
		this.nicknames = new ArrayList<String>();
		for (String name : array) {
			this.nicknames.add(name);
		}
	}

	public List<String> getNicknames() {
		return nicknames;
	}
}
