// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen;

import jodd.db.orm.DbEntityColumnDescriptor;

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
