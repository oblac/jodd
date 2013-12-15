package jodd.joy.db;

import jodd.db.oom.meta.DbId;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class EntityTest {

	@Test
	public void testEntities() {
		Foo foo = new Foo();
		Bar bar = new Bar();

		foo.id = 1;
		bar.barId = 1;

		assertFalse(foo.equals(bar));
	}



	public static class Foo extends Entity {
		long id;

		@Override
		protected long getEntityId() {
			return id;
		}

		@Override
		protected void setEntityId(long id) {
			this.id = id;
		}
	}

	public static class Bar extends IdEntity {
		@DbId
		long barId;
	}

}