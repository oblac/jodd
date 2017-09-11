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

import jodd.vtor.TestUtils;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LengthConstraintTest extends ConstraintTestBase {
    @Test
    public void testValidate_WithNullValue() {
        assertTrue("result must be true when validate a null value", LengthConstraint.validate(null, 1, 2));
    }

    @Test
    public void testConstructor1() {
        LengthConstraint lengthConstraint = new LengthConstraint();
        assertEquals("value must be default", lengthConstraint.getMin(), 0);
        assertEquals("value must be default", lengthConstraint.getMax(), 0);
    }

    @Test
    public void testConstructor2() {
        LengthConstraint lengthConstraint = new LengthConstraint(5, 10);
        assertEquals("min value must be the same as was given to constructor", lengthConstraint.getMin(), 5);
        assertEquals("max value must be the same as was given to constructor", lengthConstraint.getMax(), 10);
    }

    @Test
    public void testSetMinMax() {
        LengthConstraint lengthConstraint = new LengthConstraint();
        lengthConstraint.setMin(5);
        lengthConstraint.setMax(10);
        assertEquals("min value must be the same as was given to set method", lengthConstraint.getMin(), 5);
        assertEquals("max value must be the same as was given to set method", lengthConstraint.getMax(), 10);
    }

    @Test
    public void testConfigure() {
        LengthConstraint lengthConstraint = new LengthConstraint();
        Length annotation = mock(Length.class);
        when(annotation.min()).thenReturn(5);
        when(annotation.max()).thenReturn(10);

        lengthConstraint.configure(annotation);
        assertEquals("min value must be the same as was set to annotation when configure", lengthConstraint.getMin(), 5);
        assertEquals("max value must be the same as was set to annotation when configure", lengthConstraint.getMax(), 10);
    }

    @Test
    public void testLengthConstraint() {
        LengthConstraint lengthConstraint = new LengthConstraint(4, 6);
        assertFalse("result must be false when validate string with length 7 ", lengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(7)));
        assertFalse("result must be false when validate string with length 3", lengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(3)));
        assertTrue("result must be true when validate string with length 4", lengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(4)));
        assertTrue("result must be true when validate string with length 6", lengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(6)));
        assertTrue("result must be true when validate string with length 5", lengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(5)));
    }


}