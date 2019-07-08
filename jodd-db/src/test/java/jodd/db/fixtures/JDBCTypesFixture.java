package jodd.db.fixtures;

import jodd.buffer.FastIntBuffer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Types;

/**
 * fixture of {@link java.sql.Types JDBC Types} to catch all different {@link java.sql.Types}.
 */
public abstract class JDBCTypesFixture {

	private JDBCTypesFixture() {
		// prevents initialization
	}

	/**
	 * catch all public static final int - fields
	 *
	 * @return an array with JDBC types - never null
	 * @throws IllegalAccessException if an error occurs while accecssing fields
	 */
	public static int[] getJDBCTypes() throws IllegalAccessException {
		final FastIntBuffer jdbcTypes = new FastIntBuffer(39);

		Field[] fields = Types.class.getDeclaredFields();
		for (Field field : fields) {
			final int modifiers = field.getModifiers();
			// catch all public static final int - fields
			if (Modifier.isPublic(modifiers)
					&& Modifier.isStatic(modifiers)
					&& Modifier.isFinal(modifiers)
					&& field.getType() == int.class) {
				jdbcTypes.append(field.getInt(field));
			}
		}

		return jdbcTypes.toArray();
	}
}
