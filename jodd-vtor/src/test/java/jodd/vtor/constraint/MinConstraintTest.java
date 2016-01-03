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
import static org.mockito.Mockito.stub;

public class MinConstraintTest extends ConstraintTestBase {
    @Test
    public void testConstructor1() {
        MinConstraint minConstraint = new MinConstraint();
        assertEquals("value must be default", minConstraint.getMin(), 0.0, 0.01);
    }

    @Test
    public void testConstructor2() {
        MinConstraint minConstraint = new MinConstraint(10.0);
        assertEquals("max value must be the same as was given to constructor", minConstraint.getMin(), 10.0, 0.01);
    }

    @Test
    public void testSetMin() {
        MinConstraint minConstraint = new MinConstraint();
        minConstraint.setMin(10);
        assertEquals("method must return the same value as was given to set method", minConstraint.getMin(), 10.0, 0.01);
    }

    @Test
    public void testConfigure() {
        MinConstraint minConstraint = new MinConstraint();
        Min annotation = mock(Min.class);
        stub(annotation.value()).toReturn(10.0);

        minConstraint.configure(annotation);
        assertEquals("method must return the same value as was set to annotation when configure", minConstraint.getMin(), 10.0, 0.01);
    }

    @Test
    public void testIsValid() {
        MinConstraint minConstraint = new MinConstraint(12.5);
        assertTrue("result must be true when validate a value which is greater than minValue", minConstraint.isValid(mockContext(), "12.6"));
        assertFalse("result must be false when validate a value which is less than minValue", minConstraint.isValid(mockContext(), "12.1"));
    }

    @Test
    public void testValidate_WithValIsNull() {
        assertTrue("result must be true when validate null value", MinConstraint.validate(null, 12.5));
    }
}