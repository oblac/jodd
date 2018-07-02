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

import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.DbEntityManager;
import jodd.db.oom.sqlgen.TemplateData;

/**
 * Table chunk resolves table name, optional table alias and defines table references.
 * <p>
 * If previous chunk is also a table, comma separator will be added between two table definitions, otherwise,
 * regular space will be added if needed to separate from previous chunk.
 * <p>
 * Tables <b>must</b> be processed first, <b>before</b> all other chunks processing, since other chunks depends on it.
 */
public class TableChunk extends SqlChunk {

	protected final Class entity;
	protected final String entityName;
	protected final String tableAlias;
	protected final String tableReference;

	public TableChunk(final DbEntityManager dbEntityManager, final Object entity) {
		super(dbEntityManager, CHUNK_TABLE);
		this.entity = resolveClass(entity);
		this.entityName = null;
		this.tableAlias = this.entity.getSimpleName();
		this.tableReference = null;
	}

	public TableChunk(final DbEntityManager dbEntityManager, final Object entity, final String alias) {
		super(dbEntityManager, CHUNK_TABLE);
		this.entity = resolveClass(entity);
		this.entityName = null;
		this.tableAlias = alias;
		this.tableReference = null;
	}

	public TableChunk(final DbEntityManager dbEntityManager, final Object entity, final String alias, final String tableReference) {
		super(dbEntityManager, CHUNK_TABLE);
		this.entity = resolveClass(entity);
		this.entityName = null;
		this.tableAlias = alias;
		this.tableReference = tableReference;
	}

	public TableChunk(final DbEntityManager dbEntityManager, final String entityName, final String alias) {
		this(dbEntityManager, null, entityName, alias, null);
	}

	private TableChunk(final DbEntityManager dbEntityManager, final Class entity, final String entityName, final String tableAlias, final String tableReference) {
		super(dbEntityManager, CHUNK_TABLE);
		this.entity = entity;
		this.entityName = entityName;
		this.tableAlias = tableAlias;
		this.tableReference = tableReference;
	}

	public TableChunk(final DbEntityManager dbEntityManager, String tableRef) {
		super(dbEntityManager, CHUNK_TABLE);
		tableRef = tableRef.trim();
		int spaceNdx = tableRef.indexOf(' ');
		this.entity = null;
		if (spaceNdx != -1) {
			this.entityName = tableRef.substring(0, spaceNdx);
			String alias = tableRef.substring(spaceNdx + 1).trim();
			tableAlias = alias.length() == 0 ? null : alias;
		} else {
			this.entityName = tableRef;
			this.tableAlias = null;
		}
		this.tableReference = null;
	}

	// ---------------------------------------------------------------- process

	protected DbEntityDescriptor ded;

	/**
	 * Resolves and registers table references.
	 */
	@Override
	public void init(final TemplateData templateData) {
		super.init(templateData);
		if (entity != null) {
			ded = lookupType(entity);
		} else {
			Object object = templateData.getObjectReference(entityName);
			if (object != null) {
				ded = lookupType(resolveClass(object));
			} else {
				ded = lookupName(entityName);
			}
		}
		String tableReference = this.tableReference;

		if (tableReference == null) {
			tableReference = tableAlias;
		}
		if (tableReference == null) {
			tableReference = entityName;
		}
		if (tableReference == null) {
			tableReference = ded.getEntityName();
		}
		templateData.registerTableReference(tableReference, ded, tableAlias);
	}

	@Override
	public void process(final StringBuilder out) {
		separateByCommaOrSpace(out);
		out.append(ded.getTableNameForQuery());
		if (tableAlias != null) {
			out.append(' ').append(tableAlias);
		}
	}

}