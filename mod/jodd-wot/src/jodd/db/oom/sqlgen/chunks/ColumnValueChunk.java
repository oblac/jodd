// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.sqlgen.chunks;

import jodd.db.oom.DbEntityColumnDescriptor;

/**
 * {@link ValueChunk Value} for the <b>last</b> column.
 */
public class ColumnValueChunk extends ValueChunk {

	public ColumnValueChunk(String name, Object value) {
		this(name, value, null);
	}

	public ColumnValueChunk(String objReference) {
		this(null, null, objReference);
	}

	protected ColumnValueChunk(String name, Object value, String objReference) {
		super(name, value, objReference);
	}

	// ---------------------------------------------------------------- define
	@Override
	protected void defineParameter(StringBuilder query, String name, Object value, DbEntityColumnDescriptor dec) {
		if (dec == null) {
			dec = templateData.lastColumnDec;
		}
		super.defineParameter(query, name, value, dec);
	}

	// ---------------------------------------------------------------- clone

	@Override
	public SqlChunk clone() {
		return new ColumnValueChunk(name, value, objReference);
	}
}
