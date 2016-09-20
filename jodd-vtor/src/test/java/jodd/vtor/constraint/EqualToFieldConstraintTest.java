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

package jodd.vtor.constraint;

import jodd.vtor.ValidationConstraintContext;
import jodd.vtor.VtorException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class EqualToFieldConstraintTest extends ConstraintTestBase {
    @Test
    public void testValidate_withNullValue() {
        assertTrue("result must be true when validate null value",
                EqualToFieldConstraint.validate(new Object(), null, "someField"));
    }

    @Test
    public void testConstructor1() {
        EqualToFieldConstraint equalToFieldConstraint = new EqualToFieldConstraint();
        assertNull(equalToFieldConstraint.getFieldName());
    }

    @Test
    public void testConstructor2() {
        String fieldName = "testField";
        EqualToFieldConstraint equalToFieldConstraint = new EqualToFieldConstraint(fieldName);
        assertEquals("field name must be the same as was given to constructor", equalToFieldConstraint.getFieldName(), fieldName);
    }

    @Test
    public void testSetFieldName() {
        EqualToFieldConstraint equalToFieldConstraint = new EqualToFieldConstraint();
        String fieldName = "someField";
        equalToFieldConstraint.setFieldName(fieldName);
        assertEquals("value must be the same as was given to set method", equalToFieldConstraint.getFieldName(), fieldName);
    }

    @Test
    public void testConfigure() {
        EqualToFieldConstraint equalToFieldConstraint = new EqualToFieldConstraint();
        //set a field name through an annotation
        EqualToField fldAnnotation = mock(EqualToField.class);
        String fieldName = "anotherField";
        stub(fldAnnotation.value()).toReturn(fieldName);

        equalToFieldConstraint.configure(fldAnnotation);
        assertEquals("field name must be the same as was set to annotation when configure",
                equalToFieldConstraint.getFieldName(), fieldName);
    }

    @Test
    public void testIsValid_forEqualValues() {
        EqualToFieldConstraint equalToDeclaredFieldConstraint = new EqualToFieldConstraint("testField");
        ValidationConstraintContext cvv = mockContext();
        stub(cvv.getTarget()).toReturn(new TestBean("someValue"));

        assertTrue("result must be true when field and value are equals", equalToDeclaredFieldConstraint.isValid(cvv, "someValue"));
    }

    @Test
    public void testIsValid_forDifferentValues() {
        EqualToFieldConstraint equalToDeclaredFieldConstraint = new EqualToFieldConstraint("testField");
        ValidationConstraintContext cvv = mockContext();
        stub(cvv.getTarget()).toReturn(new TestBean("someValue"));
        assertFalse("result must be false when validated field and value are different", equalToDeclaredFieldConstraint.isValid(cvv, "wrongValue"));
    }

    @Test(expected = VtorException.class)
    public void testValidate_FieldNotFound() {
        TestBean testVal = new TestBean("someValue");
        EqualToFieldConstraint.validate(testVal, "someValue", "wrongField");
        fail("EqualToFieldConstraint should throw VtorException when receive nonexistent field name");
    }

    @Test
    public void testValidate_FieldValueIsNull() {
        TestBean testVal = new TestBean(null);
        assertFalse("result must be false when field value is null", EqualToFieldConstraint.validate(testVal, "someValue", "testField"));
    }

    public static class TestBean {
        private String testField;

        public TestBean(String testField) {
            this.testField = testField;
        }

        public String getTestField() {
            return testField;
        }

        public void setTestField(String testField) {
            this.testField = testField;
        }
    }
}