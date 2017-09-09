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
import jodd.db.oom.DbOomUtil;
import jodd.db.oom.sqlgen.DbSqlBuilderException;
import jodd.util.StringUtil;

/**
 * Renders condition part of the sql query based on values in provided entity object.
 * Conditions are defined by following expression: <code>tableRef = objectRef</code>.
 * Matching may be done for existing columns (non-null), all columns (including nulls)
 * and just for the identity columns.
 */
public class MatchChunk extends SqlChunk {

	private static final String AND = " and ";
	private static final String DEFAULT = "1=1";
	private static final String DOT_STAR = ".*";
	private static final String DOT_PLUS = ".+";
	
	protected Object data;
	protected final String tableRef;
	protected final String objectRef;
	protected final int includeColumns;

	public MatchChunk(String tableRef, Object data, int includeColumns) {
		this(tableRef, null, data, includeColumns);
	}

	public MatchChunk(String tableRef, String objectRef, int includeColumns) {
		this(tableRef, objectRef, null, includeColumns);
	}

	protected MatchChunk(String tableRef, String objectRef, Object data, int includeColumns) {
		super(CHUNK_MATCH);
		this.tableRef = tableRef;
		this.objectRef = objectRef;
		this.data = data;
		this.includeColumns = includeColumns;
	}

	public MatchChunk(String expression) {
		super(CHUNK_MATCH);
		expression = expression.trim();
		int lastNdx = expression.length();
		if (expression.endsWith(DOT_STAR)) {
			lastNdx -= 2;
			includeColumns = COLS_ALL;
		} else if (expression.endsWith(DOT_PLUS)) {
			lastNdx -= 2;
			includeColumns = COLS_ONLY_IDS;
		} else {
			includeColumns = COLS_ONLY_EXISTING;
		}
		int eq = expression.indexOf('=');
		if (eq == -1) {
			throw new DbSqlBuilderException("Syntax error, expected 'match' equality: {tableRef=objectRef}.");
		}
		tableRef = expression.substring(0, eq).trim();
		objectRef = expression.substring(eq + 1, lastNdx).trim();
	}

	@Override
	public void process(StringBuilder out) {
		if (objectRef != null) {
			data = templateData.lookupObject(objectRef);
		}

		DbEntityDescriptor ded = tableRef != null ?
			lookupTableRef(tableRef) :
			lookupType(resolveClass(data));

		String table = resolveTable(tableRef, ded);
		DbEntityColumnDescriptor[] decList = ded.getColumnDescriptors();
		String typeName = StringUtil.uncapitalize(ded.getEntityName());

		int count = 0;
		out.append('(');
		for (DbEntityColumnDescriptor dec : decList) {
			if ((includeColumns == COLS_ONLY_IDS) && (!dec.isId())) {
				continue;
			}
			String property = dec.getPropertyName();

			Object value = BeanUtil.declaredSilent.getProperty(data, property);

			if ((includeColumns == COLS_ONLY_EXISTING) && (value == null)) {
				continue;
			}

			if (includeColumns == COLS_ONLY_EXISTING) {
				if (DbOomUtil.isEmptyColumnValue(dec, value)) {
					continue;
				}
			}
			if (count > 0) {
				out.append(AND);
			}
			count++;
			out.append(table).append('.').append(dec.getColumnName()).append('=');

			String propertyName = objectRef != null ? objectRef : typeName;
			propertyName += '.' + property;
			defineParameter(out, propertyName, value, dec);
		}
		if (count == 0) {
			out.append(DEFAULT);
		}
		out.append(')');
	}

}