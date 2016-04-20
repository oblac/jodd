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

public class EqualToDeclaredFieldConstraintTest extends ConstraintTestBase {

    @Test
    public void testValidate_withNullValue() {
        assertTrue("result must be true when validate null value",
                EqualToDeclaredFieldConstraint.validate(new Object(), null, "someField"));
    }

    @Test
    public void testConstructor() {
        EqualToDeclaredFieldConstraint equalToDeclaredFieldConstraint = new EqualToDeclaredFieldConstraint();
        assertNull(equalToDeclaredFieldConstraint.getFieldName());
        String fieldName = "testField";
        equalToDeclaredFieldConstraint = new EqualToDeclaredFieldConstraint(fieldName);
        assertEquals("field name must be the same as was given to constructor", equalToDeclaredFieldConstraint.getFieldName(), fieldName);
    }

    @Test
    public void testSetFieldName() {
        EqualToDeclaredFieldConstraint equalToDeclaredFieldConstraint = new EqualToDeclaredFieldConstraint();
        String fieldName = "someField";
        equalToDeclaredFieldConstraint.setFieldName(fieldName);
        assertEquals("value must be the same as was given to set method", equalToDeclaredFieldConstraint.getFieldName(), fieldName);
    }

    @Test
    public void testConfigure() {
        EqualToDeclaredFieldConstraint equalToDeclaredFieldConstraint = new EqualToDeclaredFieldConstraint();
        //set a field name through an annotation
        EqualToDeclaredField fldAnnotation = mock(EqualToDeclaredField.class);
        String field = "anotherField";
        stub(fldAnnotation.value()).toReturn(field);

        equalToDeclaredFieldConstraint.configure(fldAnnotation);
        assertEquals("field name must be the same as was set to annotation when configure",
                equalToDeclaredFieldConstraint.getFieldName(), field);
    }

    @Test
    public void testIsValid_forEqualValues() {
        EqualToDeclaredFieldConstraint equalToDeclaredFieldConstraint = new EqualToDeclaredFieldConstraint("testField");
        ValidationConstraintContext cvv = mockContext();
        stub(cvv.getTarget()).toReturn(new TestValue("someValue"));

        assertTrue("result must be true when field and value are equals",  equalToDeclaredFieldConstraint.isValid(cvv, "someValue"));
    }

    @Test
    public void testIsValid_forDifferentValues() {
        EqualToDeclaredFieldConstraint equalToDeclaredFieldConstraint = new EqualToDeclaredFieldConstraint("testField");
        ValidationConstraintContext cvv = mockContext();
        stub(cvv.getTarget()).toReturn(new TestValue("someValue"));

        assertFalse("result must be false when validated field and value are different",  equalToDeclaredFieldConstraint.isValid(cvv, "wrongValue"));
    }

    @Test(expected = VtorException.class)
    public void testValidate_FieldNotFound() {
        TestValue testVal = new TestValue("someValue");
        EqualToDeclaredFieldConstraint.validate(testVal, "someValue", "wrongField");
        fail("EqualToDeclaredFieldConstraint should throw VtorException when receive nonexistent field name");
    }

    @Test
    public void testValidate_FieldValueIsNull() {
        TestValue testVal = new TestValue(null);
        assertFalse("result must be false when field value is null", EqualToDeclaredFieldConstraint.validate(testVal, "someValue", "testField"));
    }

    public static class TestValue {
        private String testField;

        public TestValue(String testField) {
            this.testField = testField;
        }
    }
}