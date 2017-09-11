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

public class MinLengthConstraintTest extends ConstraintTestBase {
    @Test
    public void testConstructor1() {
        MinLengthConstraint minLengthConstraint = new MinLengthConstraint();
        assertEquals("value must be default", minLengthConstraint.getMin(), 0);
    }

    @Test
    public void testConstructor2() {
        MinLengthConstraint minLengthConstraint = new MinLengthConstraint(10);
        assertEquals("value must be the same as was given to constructor", minLengthConstraint.getMin(), 10);
    }

    @Test
    public void testSetMin() {
        MinLengthConstraint minLengthConstraint = new MinLengthConstraint();
        minLengthConstraint.setMin(10);
        assertEquals("min value must be the same as was given to set method", minLengthConstraint.getMin(), 10);
    }

    @Test
    public void testConfigure() {
        MinLengthConstraint minLengthConstraint = new MinLengthConstraint();
        MinLength annotation = mock(MinLength.class);
        when(annotation.value()).thenReturn(10);

        minLengthConstraint.configure(annotation);
        assertEquals("min value must be the same as was set to annotation when configure", minLengthConstraint.getMin(), 10);
    }

    @Test
    public void testValidate_WithNullValue() {
        assertTrue("result must be true when validate a null value", MinLengthConstraint.validate(null, 1));
    }

    @Test
    public void testIsValid() {
        int min = 3;
        MinLengthConstraint minLengthConstraint = new MinLengthConstraint(min);
        assertTrue("result must be true when validate string with length greater than min", minLengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(min)));
        assertTrue("result must be true when validate string with length equal to min", minLengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(min)));
        assertFalse("result must be false when validate string with length less than min", minLengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(min - 1)));
    }
}