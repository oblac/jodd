// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen.chunks;

import jodd.db.orm.DbEntityColumnDescriptor;
import jodd.db.orm.DbEntityDescriptor;
import jodd.db.orm.sqlgen.DbSqlBuilderException;
import jodd.util.StringPool;

/**
 * Resolves column and table references. Reference is given in format: <code>tableRef.propertyName</code>.
 * Property name may be '+', indicating the identity columns. If property name is omitted, only table bame
 * will be rendered.
 */
public class ReferenceChunk extends SqlChunk {

	protected final String tableRef;
	protected final String columnRef;
	protected final boolean onlyId;

	public ReferenceChunk(String tableRef, String columnRef) {
		this(tableRef, columnRef, false);
	}

	public ReferenceChunk(String tableRef, String columnRef, boolean onlyId) {
		super(CHUNK_REFERENCE);
		this.tableRef = tableRef;
		this.columnRef = columnRef;
		this.onlyId = onlyId;
	}

	public ReferenceChunk(String reference) {
		super(CHUNK_REFERENCE);
		int dotNdx = reference.indexOf('.');
		if (dotNdx == -1) {
			this.tableRef = reference;
			this.columnRef = null;
			this.onlyId = false;
		} else {
			this.tableRef = reference.substring(0, dotNdx);
			this.columnRef = reference.substring(dotNdx + 1);
			onlyId = columnRef != null && columnRef.equals(StringPool.PLUS); 
		}
	}

	// ---------------------------------------------------------------- process

	@Override
	public void process(StringBuilder out) {
		DbEntityDescriptor ded = lookupTableRef(tableRef);
		out.append(resolveTable(tableRef, ded));

		if (onlyId == true) {
			out.append('.').append(ded.getIdColumnName());
		} else if (columnRef != null) {
			DbEntityColumnDescriptor dec = ded.findByPropertyName(columnRef);
			templateData.lastColumnDec = dec;
			String column = dec == null ? null : dec.getColumnName();
			//String column = ded.getColumnName(columnRef);
			if (column == null) {
				throw new DbSqlBuilderException("Unable to resolve column reference: '" + tableRef + '.' + columnRef + "'.");
			}
			out.append('.').append(column);
		}
	}


	// ---------------------------------------------------------------- clone

	@Override
	public SqlChunk clone() {
		return new ReferenceChunk(tableRef, columnRef, onlyId);
	}
}