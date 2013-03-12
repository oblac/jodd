// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.meta;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ActionAnnotationTest {

	@Action
	public void hello() {
	}

	@Action(value =  "value", method = "method", alias = "alias", extension = "ext", result = "result")
	public void hello2() {
	}

	@CustomAction
	public void hello3() {
	}

	@CustomAction(extension = "eee")
	public void hello4() {
	}

	@MiscAnnotation
	public void hello5() {
	}

	@MiscAnnotation(method = "mmm")
	public void hello6() {
	}

	@Test
	public void testActionAnnotationOnly() throws NoSuchMethodException {
		ActionAnnotation<Action> actionAnnotation = new ActionAnnotation<Action>(Action.class);
		assertEquals(Action.class, actionAnnotation.getAnnotationClass());

		Method method = this.getClass().getMethod("hello");
		ActionAnnotationData<Action> annotationData = actionAnnotation.readAnnotationData(method);

		assertNull(annotationData.alias);
		assertNull(annotationData.extension);
		assertNull(annotationData.method);
		assertNull(annotationData.result);
		assertNull(annotationData.value);

		method = this.getClass().getMethod("hello2");
		annotationData = actionAnnotation.readAnnotationData(method);

		assertEquals("alias", annotationData.alias);
		assertEquals("ext", annotationData.extension);
		assertEquals("method", annotationData.method);
		assertEquals("result", annotationData.result);
		assertEquals("value", annotationData.value);
	}

	@Test
	public void testCustomActionAnnotation() throws NoSuchMethodException {
		ActionAnnotation<CustomAction> actionAnnotation = new ActionAnnotation<CustomAction>(CustomAction.class);
		assertEquals(CustomAction.class, actionAnnotation.getAnnotationClass());

		Method method = this.getClass().getMethod("hello3");
		ActionAnnotationData<CustomAction> annotationData = actionAnnotation.readAnnotationData(method);

		assertEquals("ALIAS", annotationData.alias);
		assertEquals("EXT", annotationData.extension);
		assertEquals("METHOD", annotationData.method);
		assertNull(annotationData.result);
		assertNull(annotationData.value);

		method = this.getClass().getMethod("hello4");
		annotationData = actionAnnotation.readAnnotationData(method);

		assertEquals("ALIAS", annotationData.alias);
		assertEquals("eee", annotationData.extension);
		assertEquals("METHOD", annotationData.method);
		assertNull(annotationData.result);
		assertNull(annotationData.value);
	}

	@Test
	public void testMiscActionAnnotation() throws NoSuchMethodException {
		ActionAnnotation<MiscAnnotation> actionAnnotation = new ActionAnnotation<MiscAnnotation>(MiscAnnotation.class);
		assertEquals(MiscAnnotation.class, actionAnnotation.getAnnotationClass());

		Method method = this.getClass().getMethod("hello5");
		ActionAnnotationData<MiscAnnotation> annotationData = actionAnnotation.readAnnotationData(method);

		assertNull(annotationData.alias);
		assertNull(annotationData.extension);
		assertEquals("METHOD", annotationData.method);
		assertEquals("VAL", annotationData.value);
		assertNull(annotationData.result);

		method = this.getClass().getMethod("hello6");
		annotationData = actionAnnotation.readAnnotationData(method);

		assertNull(annotationData.alias);
		assertNull(annotationData.extension);
		assertEquals("mmm", annotationData.method);
		assertEquals("VAL", annotationData.value);
		assertNull(annotationData.result);
	}
}