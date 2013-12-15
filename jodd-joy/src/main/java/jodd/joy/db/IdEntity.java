package jodd.joy.db;

import jodd.db.oom.DbOomException;
import jodd.db.oom.meta.DbId;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.typeconverter.Convert;

import java.lang.reflect.Field;

/**
 * An database {@link jodd.joy.db.Entity entity} that has
 * one {@link jodd.db.oom.meta.DbId database id} field.
 * Note that this class uses reflection to read/write
 * from id field, but only for internal calculation.
 *
 * @see Entity#hashCode()
 */
public abstract class IdEntity extends Entity {

	protected final Field idField;

	protected IdEntity() {
		ClassDescriptor cd = ClassIntrospector.lookup(this.getClass());
		FieldDescriptor[] fieldDescriptors = cd.getAllFieldDescriptors();

		Field field;
		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			field = fieldDescriptor.getField();
			if (field.getAnnotation(DbId.class) != null) {
				this.idField = field;
				return;
			}
		}
		throw new DbOomException("No @DbId field.");
	}

	@Override
	protected long getEntityId() {
		try {
			return Convert.toLongValue(idField.get(this));
		} catch (IllegalAccessException iaex) {
			throw new DbOomException(iaex);
		}
	}

	@Override
	protected void setEntityId(long id) {
		try {
			idField.set(this, Long.valueOf(id));
		} catch (IllegalAccessException iaex) {
			throw new DbOomException(iaex);
		}
	}

}