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

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MaxConstraintTest extends ConstraintTestBase {

    @Test
    public void testConstructor1() {
        MaxConstraint maxConstraint = new MaxConstraint();
        assertEquals("value must be default", maxConstraint.getMax(), 0.0, 0.01);
    }

    @Test
    public void testConstructor2() {
        MaxConstraint maxConstraint = new MaxConstraint(0.1);
        assertEquals("max value must be the same as was given to constructor", maxConstraint.getMax(), 0.1, 0.01);
    }

    @Test
    public void testSetMax() {
        MaxConstraint maxConstraint = new MaxConstraint();
        maxConstraint.setMax(0.1);
        assertEquals("max value must be the same as was given to set method", maxConstraint.getMax(), 0.1, 0.01);
    }

    @Test
    public void testConfigure() {
        MaxConstraint maxConstraint = new MaxConstraint();
        Max annotation = mock(Max.class);
        when(annotation.value()).thenReturn(0.1);

        maxConstraint.configure(annotation);
        assertEquals("max value must be the same as was set to annotation when configure", maxConstraint.getMax(), 0.1, 0.01);    }


    @Test
    public void testValidate_WithNullValue() {
        assertTrue("result must be true when validate a null value", MaxConstraint.validate(null, 12.1));
    }

    @Test
    public void testIsValid() {
        assertTrue("result must be true when validated value is less than max", new MaxConstraint(12.5).isValid(mockContext(), "12.1"));
        assertFalse("result must be true when validated value is grater than max", new MaxConstraint(12.5).isValid(mockContext(), "12.6"));
    }
}