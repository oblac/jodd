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
package jodd.db;

import jodd.util.collection.IntArrayList;

import java.sql.CallableStatement;
import java.sql.SQLException;

public class DbCallResult {
	private final DbQueryParser query;
	private final CallableStatement statement;

	DbCallResult(DbQueryParser parser, CallableStatement callableStatement) {
		this.query = parser;
		this.statement = callableStatement;
	}

	// ---------------------------------------------------------------- integer

	public int getInteger(int index) {
		try {
			return statement.getInt(index);
		} catch (SQLException sex) {
			throw newGetParamError(index, sex);
		}
	}

	public int getInteger(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			if (positions.size() == 1) {
				return statement.getInt(positions.get(0));
			}
			throw newGetParamError(param);
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}

	public int[] getAllInteger(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		int[] result = new int[positions.size()];
		try {
			for (int i = 0; i < positions.size(); i++) {
				result[i] = statement.getInt(positions.get(i));
			}
			return result;
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- boolean

	public boolean getBoolean(int index) {
		try {
			return statement.getBoolean(index);
		} catch (SQLException sex) {
			throw newGetParamError(index, sex);
		}
	}

	public boolean getBoolean(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			if (positions.size() == 1) {
				return statement.getBoolean(positions.get(0));
			}
			throw newGetParamError(param);
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}

	public boolean[] getAllBoolean(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		boolean[] result = new boolean[positions.size()];
		try {
			for (int i = 0; i < positions.size(); i++) {
				result[i] = statement.getBoolean(positions.get(i));
			}
			return result;
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- byte

	public byte getByte(int index) {
		try {
			return statement.getByte(index);
		} catch (SQLException sex) {
			throw newGetParamError(index, sex);
		}
	}

	public byte getByte(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			if (positions.size() == 1) {
				return statement.getByte(positions.get(0));
			}
			throw newGetParamError(param);
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}

	public byte[] getAllByte(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		byte[] result = new byte[positions.size()];
		try {
			for (int i = 0; i < positions.size(); i++) {
				result[i] = statement.getByte(positions.get(i));
			}
			return result;
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- doublw

	public double getDouble(int index) {
		try {
			return statement.getDouble(index);
		} catch (SQLException sex) {
			throw newGetParamError(index, sex);
		}
	}

	public double getDouble(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			if (positions.size() == 1) {
				return statement.getDouble(positions.get(0));
			}
			throw newGetParamError(param);
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}

	public double[] getAllDouble(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		double[] result = new double[positions.size()];
		try {
			for (int i = 0; i < positions.size(); i++) {
				result[i] = statement.getDouble(positions.get(i));
			}
			return result;
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}

	// ---------------------------------------------------------------- float

	public float getFloat(int index) {
		try {
			return statement.getFloat(index);
		} catch (SQLException sex) {
			throw newGetParamError(index, sex);
		}
	}

	public float getFloat(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			if (positions.size() == 1) {
				return statement.getFloat(positions.get(0));
			}
			throw newGetParamError(param);
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}

	public float[] getAllFloat(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		float[] result = new float[positions.size()];
		try {
			for (int i = 0; i < positions.size(); i++) {
				result[i] = statement.getFloat(positions.get(i));
			}
			return result;
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}


	// ---------------------------------------------------------------- string

	public String getString(int index) {
		try {
			return statement.getString(index);
		} catch (SQLException sex) {
			throw newGetParamError(index, sex);
		}
	}

	public String getString(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		try {
			if (positions.size() == 1) {
				return statement.getString(positions.get(0));
			}
			throw newGetParamError(param);
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}

	public String[] getAllString(String param) {
		IntArrayList positions = query.getNamedParameterIndices(param);
		String[] result = new String[positions.size()];
		try {
			for (int i = 0; i < positions.size(); i++) {
				result[i] = statement.getString(positions.get(i));
			}
			return result;
		} catch (SQLException sex) {
			throw newGetParamError(param, sex);
		}
	}


	// ---------------------------------------------------------------- exception

	private DbSqlException newGetParamError(int index, Exception ex) {
		return new DbSqlException("Invalid SQL parameter with index: " + index, ex);
	}
	private DbSqlException newGetParamError(String param, Exception ex) {
		return new DbSqlException("Invalid SQL parameter with name: " + param, ex);
	}
	private DbSqlException newGetParamError(String param) {
		return new DbSqlException("Invalid number of parameter with name: " + param);
	}

}
