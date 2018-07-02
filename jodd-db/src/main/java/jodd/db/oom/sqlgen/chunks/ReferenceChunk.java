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

package jodd.db.oom.sqlgen.chunks;

import jodd.db.oom.DbEntityColumnDescriptor;
import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.DbEntityManager;
import jodd.db.oom.sqlgen.DbSqlBuilderException;
import jodd.util.StringPool;

/**
 * Resolves column and table references. Reference is given in format: <code>tableRef.propertyName</code>.
 * The <code>propertyName</code> may be '+' (e.g. <code>tableRef.+</code>), indicating the identity columns.
 * If property name is omitted (e.g. <code>tableRef</code>), only table name will be rendered.
 * If table reference is omitted (e.g. <code>.propertyName</code>), only property i.e. column name
 * will be rendered.
 */
public class ReferenceChunk extends SqlChunk {

	protected final String tableRef;
	protected final String columnRef;
	protected final boolean onlyId;

	public ReferenceChunk(final DbEntityManager dbEntityManager, final String tableRef, final String columnRef) {
		this(dbEntityManager, tableRef, columnRef, false);
	}

	public ReferenceChunk(final DbEntityManager dbEntityManager, final String tableRef, final String columnRef, final boolean onlyId) {
		super(dbEntityManager, CHUNK_REFERENCE);
		this.tableRef = tableRef;
		this.columnRef = columnRef;
		this.onlyId = onlyId;
	}

	public ReferenceChunk(final DbEntityManager dbEntityManager, final String reference) {
		super(dbEntityManager, CHUNK_REFERENCE);

		int dotNdx = reference.indexOf('.');

		if (dotNdx == -1) {
			this.tableRef = reference;
			this.columnRef = null;
			this.onlyId = false;
		} else {
			String ref = reference.substring(0, dotNdx);
			if (ref.length() == 0) {
				ref = null;
			}
			this.tableRef = ref;

			ref = reference.substring(dotNdx + 1);
			if (ref.length() == 0) {
				ref = null;
			}
			this.columnRef = ref;
			onlyId = columnRef != null && columnRef.equals(StringPool.PLUS);
		}
	}

	// ---------------------------------------------------------------- process

	@Override
	public void process(final StringBuilder out) {

		final DbEntityDescriptor ded;

		if (tableRef != null) {
			ded = lookupTableRef(tableRef);

			final String tableName = resolveTable(tableRef, ded);

			out.append(tableName);
		} else {
			ded = findColumnRef(columnRef);
		}


		if (onlyId) {
			if (tableRef != null) {
				out.append('.');
			}
			out.append(ded.getIdColumnName());
		} else if (columnRef != null) {
			DbEntityColumnDescriptor dec = ded.findByPropertyName(columnRef);
			templateData.lastColumnDec = dec;

			if (dec == null) {
				throw new DbSqlBuilderException("Invalid column reference: [" + tableRef + '.' + columnRef + "]");
			}

			if (tableRef != null) {
				out.append('.');
			}
			out.append(dec.getColumnNameForQuery());
		}
	}

}