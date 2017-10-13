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

}