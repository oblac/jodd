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

import jodd.util.annotation.AnnotationParser;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
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
		Method method = this.getClass().getMethod("hello");

		ActionAnnotationValues annotationValues = annValueOf(method);

		assertNull(annotationValues.alias());
		assertNull(annotationValues.value());

		method = this.getClass().getMethod("hello2");
		annotationValues = annValueOf(method);

		assertEquals("alias", annotationValues.alias);
		assertEquals("value.ext", annotationValues.value);
	}

	@Test
	void testCustomActionAnnotation() throws NoSuchMethodException {
		final AnnotationParser annotationParser = parserFor(CustomAction.class);

		Method method = this.getClass().getMethod("hello3");
		ActionAnnotationValues annotationValues = ActionAnnotationValues.of(annotationParser, method);

		assertEquals("ALIAS", annotationValues.alias());
		assertNull(annotationValues.value());

		method = this.getClass().getMethod("hello4");
		annotationValues = ActionAnnotationValues.of(annotationParser, method);

		assertEquals("ALIAS", annotationValues.alias());
		assertNull(annotationValues.value());
	}

	@Test
	void testMiscActionAnnotation() throws NoSuchMethodException {
		final AnnotationParser annotationParser = parserFor(MiscAnnotation.class);

		Method method = this.getClass().getMethod("hello5");
		ActionAnnotationValues annotationValues = ActionAnnotationValues.of(annotationParser, method);

		assertNull(annotationValues.alias());
		assertEquals("VAL", annotationValues.value());

		method = this.getClass().getMethod("hello6");
		annotationValues = ActionAnnotationValues.of(annotationParser, method);

		assertNull(annotationValues.alias());
		assertEquals("VAL", annotationValues.value());
	}


	/**
	 * Shortcut methods for given annotation class.
	 */
	public static AnnotationParser parserFor(final Class<? extends Annotation> annotationClass) {
		return new AnnotationParser(annotationClass, Action.class);
	}

	/**
	 * Shortcut method assuming default annotation.
	 */
	public static ActionAnnotationValues annValueOf(final AnnotatedElement annotatedElement) {
		return ActionAnnotationValues.of(new AnnotationParser(Action.class), annotatedElement);
	}


}
