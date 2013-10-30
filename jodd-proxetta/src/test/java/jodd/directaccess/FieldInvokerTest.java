package jodd.directaccess;

import org.junit.Test;

import static jodd.directaccess.FieldInvokerClassBuilder.createNewInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FieldInvokerTest {

	@Test
	public void testField1() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		FieldInvoker fieldInvoker = createNewInstance(SomeClass.class.getField("field1"));

		SomeClass someClass = new SomeClass();
		fieldInvoker.set(someClass, "JODD");
		assertEquals("JODD", someClass.field1);
		assertEquals("JODD", fieldInvoker.get(someClass));
	}

	@Test
	public void testField2() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		FieldInvoker fieldInvoker = createNewInstance(SomeClass.class.getField("field2"));

		SomeClass someClass = new SomeClass();
		fieldInvoker.set(someClass, 173);
		assertEquals(173, someClass.field2);
		assertEquals(173, fieldInvoker.get(someClass));
	}

	@Test
	public void testField3() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		FieldInvoker fieldInvoker = createNewInstance(SomeClass.class.getField("field3"));

		SomeClass someClass = new SomeClass();
		fieldInvoker.set(someClass, 173L);
		assertEquals(173L, someClass.field3);
		assertEquals(173L, fieldInvoker.get(someClass));
	}

	@Test
	public void testField4() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		FieldInvoker fieldInvoker = createNewInstance(SomeClass.class.getField("field4"));

		SomeClass someClass = new SomeClass();
		fieldInvoker.set(someClass, 17.1f);
		assertEquals(17.1f, someClass.field4, 0.1);
		assertEquals(17.1f, fieldInvoker.get(someClass));
	}

	@Test
	public void testField5() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		FieldInvoker fieldInvoker = createNewInstance(SomeClass.class.getField("field5"));

		SomeClass someClass = new SomeClass();
		fieldInvoker.set(someClass, 17.1d);
		assertEquals(17.1d, someClass.field5, 0.1);
		assertEquals(17.1d, fieldInvoker.get(someClass));
	}

	@Test
	public void testField6() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		FieldInvoker fieldInvoker = createNewInstance(SomeClass.class.getField("field6"));

		SomeClass someClass = new SomeClass();
		fieldInvoker.set(someClass, (byte)17);
		assertEquals((byte)17, someClass.field6);
		assertEquals((byte)17, fieldInvoker.get(someClass));
	}

	@Test
	public void testField7() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		FieldInvoker fieldInvoker = createNewInstance(SomeClass.class.getField("field7"));

		SomeClass someClass = new SomeClass();
		fieldInvoker.set(someClass, (short)17);
		assertEquals((short)17, someClass.field7);
		assertEquals((short)17, fieldInvoker.get(someClass));
	}

	@Test
	public void testField8() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		FieldInvoker fieldInvoker = createNewInstance(SomeClass.class.getField("field8"));

		SomeClass someClass = new SomeClass();
		fieldInvoker.set(someClass, true);
		assertTrue(someClass.field8);
		assertEquals(true, fieldInvoker.get(someClass));
	}

}