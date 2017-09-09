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

import jodd.bean.BeanUtil;
import jodd.db.oom.DbEntityColumnDescriptor;
import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.DbOomManager;
import jodd.db.oom.DbOomUtil;
import jodd.util.StringUtil;

/**
 * Generates the SET part of the UPDATE statement.
 * It may contains only non-<code>null</code> values, or all.
 */
public class UpdateSetChunk extends SqlChunk {

	private static final String SET = "set ";
	
	protected final Object data;
	protected final String tableRef;
	protected final int includeColumns;

	public UpdateSetChunk(String tableRef, Object data, int includeColumns) {
		super(CHUNK_UPDATE);
		this.tableRef = tableRef;
		this.data = data;
		this.includeColumns = includeColumns;
	}

	@Override
	public void process(StringBuilder out) {
		if (isPreviousChunkOfType(CHUNK_TABLE)) {
			appendMissingSpace(out);
		}

		DbEntityDescriptor ded = tableRef != null ?
				lookupTableRef(tableRef) :
				lookupType(resolveClass(data));

		out.append(SET);

		DbEntityColumnDescriptor[] decList = ded.getColumnDescriptors();
		String typeName = StringUtil.uncapitalize(ded.getEntityName());
		//String table = resolveTable(tableRef, ded);

		int size = 0;
		for (DbEntityColumnDescriptor dec : decList) {

			if (dec.isId() && !DbOomManager.getInstance().getSqlGenConfig().isUpdateablePrimaryKey()) {
				continue;
			}

			String property = dec.getPropertyName();
			Object value = BeanUtil.declared.getProperty(data, property);

			if (includeColumns == COLS_ONLY_EXISTING) {
				if (DbOomUtil.isEmptyColumnValue(dec, value)) {
					continue;
				}
			}

			if (size > 0) {
				out.append(',').append(' ');
			}

			size++;


			// do not add table reference in set
			// as only one table can be updated
			// also, Postgress database does not allow it (see #JODD-21)

			//out.append(table).append('.');

			out.append(dec.getColumnName()).append('=');

			String propertyName = typeName + '.' + property;
			defineParameter(out, propertyName, value, dec);
		}
		if (size > 0) {
			out.append(' ');
		}
	}

}