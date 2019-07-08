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
import jodd.db.oom.DbEntityManager;
import jodd.util.StringUtil;

/**
 * Renders complete INSERT statement. This chunk is a bit different than others since it renders a complete SQL
 * query for inserting.
 */
public class InsertChunk extends SqlChunk {

	protected final String entityName;
	protected final Class entityType;
	protected final Object data;
	protected final boolean defaultIsUpdateablePrimaryKey;

	public InsertChunk(
			final DbEntityManager dbEntityManager,
			final boolean isUpdateablePrimaryKey,
			final String entityName,
			final Object data) {
		this(dbEntityManager, isUpdateablePrimaryKey, entityName, null, data);
	}

	public InsertChunk(
			final DbEntityManager dbEntityManager,
			final boolean isUpdateablePrimaryKey,
			final Class entityType,
			final Object data) {
		this(dbEntityManager, isUpdateablePrimaryKey, null, entityType, data);
	}

	private InsertChunk(
			final DbEntityManager dbEntityManager,
			final boolean isUpdateablePrimaryKey,
			final String entityName,
			final Class entityType,
			final Object data) {
		super(dbEntityManager, CHUNK_INSERT);
		this.defaultIsUpdateablePrimaryKey = isUpdateablePrimaryKey;
		this.entityName = entityName;
		this.entityType = entityType;
		this.data = data;
	}

	@Override
	public void process(final StringBuilder out) {
		DbEntityDescriptor ded = entityName != null ? lookupName(entityName) : lookupType(entityType);
		StringBuilder col = new StringBuilder();
		StringBuilder val = new StringBuilder();

		DbEntityColumnDescriptor[] decList = ded.getColumnDescriptors();
		String typeName = StringUtil.uncapitalize(ded.getEntityName());

		int size = 0;
		for (DbEntityColumnDescriptor dec : decList) {
			 if (dec.isId() && !defaultIsUpdateablePrimaryKey) {
			 	continue;
			 }

			String property = dec.getPropertyName();
			Object value = BeanUtil.declared.getProperty(data, property);
			if (value == null) {
				continue;
			}
			
			if (size > 0) {
				col.append(',').append(' ');
				val.append(',').append(' ');
			}
			size++;
			col.append(dec.getColumnNameForQuery());

			final String propertyName = typeName + '.' + property;
			defineParameter(val, propertyName, value, dec);
		}

		out.append("insert into ").append(ded.getTableNameForQuery()).append(" (")
				.append(col).append(") values (").append(val).append(')');
	}

}