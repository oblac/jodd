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

package jodd.madvoc.meta;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActionAnnotationTest {

	@Action
	public void hello() {
	}

	@Action(value =  "value.ext", alias = "alias")
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
	void testActionAnnotationOnly() throws NoSuchMethodException {
		ActionAnnotation<Action> actionAnnotation = new ActionAnnotation<>(Action.class);
		assertEquals(Action.class, actionAnnotation.getAnnotationClass());

		Method method = this.getClass().getMethod("hello");
		ActionAnnotationData<Action> annotationData = actionAnnotation.readAnnotatedElement(method);

		assertNull(annotationData.alias);
		assertNull(annotationData.value);

		method = this.getClass().getMethod("hello2");
		annotationData = actionAnnotation.readAnnotatedElement(method);

		assertEquals("alias", annotationData.alias);
		assertEquals("value.ext", annotationData.value);
	}

	@Test
	void testCustomActionAnnotation() throws NoSuchMethodException {
		ActionAnnotation<CustomAction> actionAnnotation = new ActionAnnotation<>(CustomAction.class);
		assertEquals(CustomAction.class, actionAnnotation.getAnnotationClass());

		Method method = this.getClass().getMethod("hello3");
		ActionAnnotationData<CustomAction> annotationData = actionAnnotation.readAnnotatedElement(method);

		assertEquals("ALIAS", annotationData.alias);
		assertNull(annotationData.value);

		method = this.getClass().getMethod("hello4");
		annotationData = actionAnnotation.readAnnotatedElement(method);

		assertEquals("ALIAS", annotationData.alias);
		assertNull(annotationData.value);
	}

	@Test
	void testMiscActionAnnotation() throws NoSuchMethodException {
		ActionAnnotation<MiscAnnotation> actionAnnotation = new ActionAnnotation<>(MiscAnnotation.class);
		assertEquals(MiscAnnotation.class, actionAnnotation.getAnnotationClass());

		Method method = this.getClass().getMethod("hello5");
		ActionAnnotationData<MiscAnnotation> annotationData = actionAnnotation.readAnnotatedElement(method);

		assertNull(annotationData.alias);
		assertEquals("VAL", annotationData.value);

		method = this.getClass().getMethod("hello6");
		annotationData = actionAnnotation.readAnnotatedElement(method);

		assertNull(annotationData.alias);
		assertEquals("VAL", annotationData.value);
	}
}
