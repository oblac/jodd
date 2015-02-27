// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.tst;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbTable;

@DbTable
public class Enumerator {

	public static enum STATUS {
		ONE(1),
		TWO(123),
		THREE(222);

		final int value;

		private STATUS(int i) {
			this.value = i;
		}

		public int value() {
			return value;
		}

		@Override
		public String toString() {
			return Integer.toString(value);
		}
	}


	@DbId public long id;
	@DbColumn public String name;
	@DbColumn public STATUS status;


}