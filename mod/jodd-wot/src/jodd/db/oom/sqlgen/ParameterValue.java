// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.sqlgen;

import jodd.db.oom.DbEntityColumnDescriptor;

/**
 * {@link TemplateData Template parameter} value.
 */
public class ParameterValue {
	protected final Object value;
	protected final DbEntityColumnDescriptor dec;

	public ParameterValue(Object value, DbEntityColumnDescriptor dec) {
		this.value = value;
		this.dec = dec;
	}

	public Object getValue() {
		return value;
	}

	public DbEntityColumnDescriptor getColumnDescriptor() {
		return dec;
	}

}
