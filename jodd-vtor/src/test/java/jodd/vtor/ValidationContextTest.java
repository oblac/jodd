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

package jodd.vtor;

import jodd.introspector.PropertyDescriptor;
import jodd.vtor.constraint.Max;
import jodd.vtor.constraint.Min;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ValidationContextTest {

    @Test
    public void testResolveFor() throws Exception {
        //when
        //Class ForCheck1 contains two fields. field1 has two constraints. Second field2 has a non constraint annotation
        ValidationContext context = ValidationContext.resolveFor(ClassForCheck1.class);

        //then
        assertEquals(context.map.size(), 1, "context must return a map with one element when resolve an object which has one field with constraint annotations");
        assertNotNull(context.map.get("field1"), "context must return a map with key field1 when resolve an object which has field with name field1 and constraint annotations");
        assertNull(context.map.get("field2"), "context must not return a map with key field2 when resolve an object which has a field with name field2 without constraint annotations");
        assertEquals(context.map.get("field1").size(), 2, "context must return a map with two checks when resolve an object which has a field with two constraint annotations");
    }

    @Test
    public void testAddClassThrowVtorException() throws Exception {
        ValidationContext context = new ValidationContext() {
            @Override
            protected <V extends ValidationConstraint> V newConstraint(Class<V> constraint, Class targetType) throws Exception {
                throw new RuntimeException("terrible error");
            }
        };
        assertThrows(VtorException.class, () -> context.addClassChecks(ClassForCheck1.class));
    }

    @Test
    public void testCollectPropertyAnnotationChecks(){
        ValidationContext context = spy(new ValidationContext());
        PropertyDescriptor propertyDescriptor = mock(PropertyDescriptor.class);
        List<Check> list = new ArrayList<>();
        context.collectPropertyAnnotationChecks(list, propertyDescriptor);
        verify(context, never()).collectAnnotationChecks(any(List.class), any(Class.class),any(String.class), any(Annotation[].class));
    }

    @Test
    public void testAddClassChecksWithCache() throws Exception {
        ValidationContext context = spy(new ValidationContext());
        try {
            context.addClassChecks(ClassForCheck1.class);
            context.addClassChecks(ClassForCheck1.class);
        } finally {
            context.clearCache();
        }

        //collectProperty must be invoked only for first call of addClassChecks. Two calls for two annotations
        verify(context, times(2)).collectPropertyAnnotationChecks(any(List.class), any(PropertyDescriptor.class));
    }

    private static class ClassForCheck1 {
        @Max(20)
        @Min(10)
        private String field1;

        @NotConstraint
        private String field2;

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NotConstraint {
    }

}
