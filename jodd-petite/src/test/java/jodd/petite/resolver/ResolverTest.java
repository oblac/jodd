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

package jodd.petite.resolver;

import jodd.introspector.ClassIntrospector;
import jodd.introspector.PropertyDescriptor;
import jodd.petite.PetiteConfig;
import jodd.petite.PetiteException;
import jodd.petite.def.BeanReferences;
import jodd.petite.fixtures.Revolver;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResolverTest {

	ReferencesResolver referencesResolver = new ReferencesResolver(new PetiteConfig());

	@Test
	void testNoAnnotation() {
		Arrays.asList(
			method(Revolver.class, "noAnnotation"),
			ctor(Revolver.NoAnnotation.class)
		).forEach(methodOrCtor -> {
			BeanReferences[] refs = referencesResolver.readAllReferencesFromAnnotation(method(Revolver.class, "noAnnotation"));
			assertEquals(null, refs);
		});
	}

	@Test
	void testAnnotation_noArguments() {
		Arrays.asList(
			method(Revolver.class, "onlyAnnotation"),
			ctor(Revolver.OnlyAnnotation.class)
		).forEach(methodOrCtor -> {
			BeanReferences[] refs = referencesResolver.readAllReferencesFromAnnotation(methodOrCtor);

			assertEquals(0, refs.length);
		});
	}

	@Test
	void testAnnotation_withArguments() {
		Arrays.asList(
				method(Revolver.class, "someArguments"),
				ctor(Revolver.SomeArguments.class)
		).forEach(methodOrCtor -> {
			BeanReferences[] refs = referencesResolver.readAllReferencesFromAnnotation(methodOrCtor);

			assertEquals(2, refs.length);

			assertEquals(3, refs[0].size());
			assertEquals("in1", refs[0].name(0));
			assertEquals("string", refs[0].name(1));
			assertEquals("java.lang.String", refs[0].name(2));

			assertEquals(3, refs[1].size());

			assertEquals("in2", refs[1].name(0));
			assertEquals("integer", refs[1].name(1));
			assertEquals("java.lang.Integer", refs[1].name(2));
		});
	}

	@Test
	void testAnnotation_withCsvValue() {
		Arrays.asList(
			method(Revolver.class, "someArguments_csv"),
			ctor(Revolver.SomeArguments_csv.class)
		).forEach(methodOrCtor -> {
			BeanReferences[] refs = referencesResolver.readAllReferencesFromAnnotation(methodOrCtor);

			assertEquals(2, refs.length);

			assertEquals(1, refs[0].size());
			assertEquals("innn1", refs[0].name(0));

			assertEquals(1, refs[1].size());

			assertEquals("innn2", refs[1].name(0));
		});
	}

	@Test
	void testAnnotation_withWrongValue() {
		Arrays.asList(
			method(Revolver.class, "someArguments_wrongAnnotation"),
			ctor(Revolver.SomeArguments_wrongAnnotation.class)
		).forEach(methodOrCtor -> {
			assertThrows(PetiteException.class, () ->
				referencesResolver.readAllReferencesFromAnnotation(methodOrCtor));
		});
	}

	@Test
	void testAnnotation_noMethodArguments() {
		Arrays.asList(
			method(Revolver.class, "noMethodArgument"),
			ctor(Revolver.NoMethodArgument.class)
		).forEach(methodOrCtor -> {
			BeanReferences[] refs = referencesResolver.readAllReferencesFromAnnotation(methodOrCtor);

			assertEquals(2, refs.length);

			assertEquals(3, refs[0].size());
			assertEquals("in1", refs[0].name(0));
			assertEquals("string", refs[0].name(1));
			assertEquals("java.lang.String", refs[0].name(2));
			
			assertEquals(3, refs[1].size());

			assertEquals("in2", refs[1].name(0));
			assertEquals("integer", refs[1].name(1));
			assertEquals("java.lang.Integer", refs[1].name(2));
		});
	}

	@Test
	void testAnnotation_noMethodArguments_partial() {
		Arrays.asList(
			method(Revolver.class, "noMethodArgument_partial"),
			ctor(Revolver.NoMethodArgument_partial.class)
		).forEach(methodOrCtor -> {
			BeanReferences[] refs = referencesResolver.readAllReferencesFromAnnotation(methodOrCtor);

			assertEquals(2, refs.length);
			
			assertEquals(1, refs[0].size());
			assertEquals("innn1", refs[0].name(0));

			assertEquals(3, refs[1].size());

			assertEquals("in2", refs[1].name(0));
			assertEquals("integer", refs[1].name(1));
			assertEquals("java.lang.Integer", refs[1].name(2));
		});
	}

	@Test
	void testAnnotation_mix() {
		Arrays.asList(
			method(Revolver.class, "mix"),
			ctor(Revolver.Mix.class)
		).forEach(methodOrCtor -> {
			BeanReferences[] refs = referencesResolver.readAllReferencesFromAnnotation(methodOrCtor);

			assertEquals(2, refs.length);

			assertEquals(1, refs[0].size());
			assertEquals("innn1", refs[0].name(0));

			assertEquals(1, refs[1].size());

			assertEquals("bbbb", refs[1].name(0));
		});
	}

	@Test
	void testFieldAnnotation_annotation() {
		BeanReferences refs = referencesResolver.readReferenceFromAnnotation(field(Revolver.class, "onlyAnnotation"));
		assertEquals(3, refs.size());
		assertEquals("onlyAnnotation", refs.name(0));
		assertEquals("string", refs.name(1));
		assertEquals("java.lang.String", refs.name(2));
	}
	@Test
	void testFieldAnnotation_withValue() {
		BeanReferences refs = referencesResolver.readReferenceFromAnnotation(field(Revolver.class, "valued"));
		assertEquals(1, refs.size());
		assertEquals("inn", refs.name(0));
	}
	@Test
	void testFieldAnnotation_nothing() {
		BeanReferences refs = referencesResolver.readReferenceFromAnnotation(field(Revolver.class, "nothing"));

		assertEquals(null, refs);
	}


	@Test
	void testResolveReferenceFromValues_explicit() {
		Arrays.asList(
			method(Revolver.class, "someArguments"),
			ctor(Revolver.SomeArguments.class)
		).forEach(methodOrCtor -> {
			BeanReferences[] refs = referencesResolver.resolveReferenceFromValues(methodOrCtor, "i1", "i2");

			assertEquals(2, refs.length);

			assertEquals(1, refs[0].size());
			assertEquals("i1", refs[0].name(0));

			assertEquals(1, refs[1].size());

			assertEquals("i2", refs[1].name(0));
		});
	}

	@Test
	void testResolveReferenceFromValues_defaults() {
		Arrays.asList(
			method(Revolver.class, "someArguments"),
			ctor(Revolver.SomeArguments.class)
		).forEach(methodOrCtor -> {
			BeanReferences[] refs = referencesResolver.resolveReferenceFromValues(methodOrCtor);

			assertEquals(2, refs.length);

			assertEquals(3, refs[0].size());
			assertEquals("in1", refs[0].name(0));
			assertEquals("string", refs[0].name(1));
			assertEquals("java.lang.String", refs[0].name(2));

			assertEquals(3, refs[1].size());

			assertEquals("in2", refs[1].name(0));
			assertEquals("integer", refs[1].name(1));
			assertEquals("java.lang.Integer", refs[1].name(2));

		});
	}

	@Test
	void testResolveReferenceFromValues_invalidNumberOfRefs() {
		Arrays.asList(
			method(Revolver.class, "someArguments"),
			ctor(Revolver.SomeArguments.class)
		).forEach(methodOrCtor -> {
			assertThrows(PetiteException.class, () -> {
				referencesResolver.resolveReferenceFromValues(methodOrCtor, "i1");
			});
		});
	}

	// ---------------------------------------------------------------- util

	private PropertyDescriptor field(Class type, String name) {
		PropertyDescriptor[] propertyDescriptors = ClassIntrospector.get().lookup(type).getAllPropertyDescriptors();

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (propertyDescriptor.getName().equals(name)) {
				return propertyDescriptor;
			}
		}
		throw new IllegalArgumentException();
	}

	private Method method(Class type, String name) {
		Method[] methods = type.getMethods();

		for (Method method : methods) {
			if (method.getName().equals(name)) {
				return method;
			}
		}
		throw new IllegalArgumentException();
	}

	private Constructor ctor(Class type) {
		return type.getConstructors()[0];
	}

}