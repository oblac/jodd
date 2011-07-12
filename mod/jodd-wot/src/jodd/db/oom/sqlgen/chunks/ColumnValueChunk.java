// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen.chunks;

import jodd.db.orm.DbEntityColumnDescriptor;

/**
 * {@link jodd.db.orm.sqlgen.chunks.ValueChunk Value} for the last column.
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
