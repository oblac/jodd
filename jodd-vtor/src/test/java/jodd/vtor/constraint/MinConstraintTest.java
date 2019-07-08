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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MinConstraintTest extends ConstraintTestBase {
    @Test
    void testConstructor1() {
        MinConstraint minConstraint = new MinConstraint();
        assertEquals(0.0, minConstraint.getMin(), 0.01);
    }

    @Test
    void testConstructor2() {
        MinConstraint minConstraint = new MinConstraint(10.0);
        assertEquals(10.0, minConstraint.getMin(), 0.01);
    }

    @Test
    void testSetMin() {
        MinConstraint minConstraint = new MinConstraint();
        minConstraint.setMin(10);
        assertEquals(10.0, minConstraint.getMin(), 0.01);
    }

    @Test
    void testConfigure() {
        MinConstraint minConstraint = new MinConstraint();
        Min annotation = mock(Min.class);
        when(annotation.value()).thenReturn(10.0);

        minConstraint.configure(annotation);
        assertEquals(10.0, minConstraint.getMin(), 0.01);
    }

    @Test
    void testIsValid() {
        MinConstraint minConstraint = new MinConstraint(12.5);
        assertTrue(minConstraint.isValid(mockContext(), "12.6"));
        assertFalse(minConstraint.isValid(mockContext(), "12.1"));
    }

    @Test
    void testValidate_WithValIsNull() {
        assertTrue(MinConstraint.validate(null, 12.5));
    }
}
