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
