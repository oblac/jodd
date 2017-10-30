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

package jodd.db.oom.mapper;

import jodd.db.oom.DbEntityManager;

import java.sql.ResultSet;

/**
 * ResultSet mapper which implementations parse objects from one result set row.
 * There are two ways of mapping. The basic way is mapping against provided
 * entity types. The second, extended, way is auto-mapping, where no types
 * are provided. Instead, they are mapped by {@link DbEntityManager} or
 * similar external class.
 * <p>
 * There should be only one instance of <code>ResultSetMapper</code> per <code>ResultSet</code>.
 */
public interface ResultSetMapper {

	// ---------------------------------------------------------------- moving

	/**
	 * Moves the cursor down one row from its current position.
	 */
	boolean next();

	/**
	 * Releases this ResultSet object's database and JDBC resources immediately instead of
	 * waiting for this to happen when it is automatically closed.
	 */
	void close();

	/**
	 * Return JDBC result set.
	 */
	ResultSet getResultSet();


	// ---------------------------------------------------------------- parse types

	/**
	 * Resolves table names into the list of entity types.
	 * Resolving is used when query is executed without specified types.
	 */
	Class[] resolveTables();

	/**
	 * Parse objects from one result set row to specified types.
	 */
	Object[] parseObjects(Class... types);

	/**
	 * Parse single object from result set row to specified type.
	 * @see #parseObjects(Class[]) 
	 */
	Object parseOneObject(Class... types);

}
