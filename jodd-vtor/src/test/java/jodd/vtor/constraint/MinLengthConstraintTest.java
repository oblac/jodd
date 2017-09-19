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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MinLengthConstraintTest extends ConstraintTestBase {
    @Test
    public void testConstructor1() {
        MinLengthConstraint minLengthConstraint = new MinLengthConstraint();
        assertEquals(0, minLengthConstraint.getMin());
    }

    @Test
    public void testConstructor2() {
        MinLengthConstraint minLengthConstraint = new MinLengthConstraint(10);
        assertEquals(10, minLengthConstraint.getMin());
    }

    @Test
    public void testSetMin() {
        MinLengthConstraint minLengthConstraint = new MinLengthConstraint();
        minLengthConstraint.setMin(10);
        assertEquals(10, minLengthConstraint.getMin());
    }

    @Test
    public void testConfigure() {
        MinLengthConstraint minLengthConstraint = new MinLengthConstraint();
        MinLength annotation = mock(MinLength.class);
        when(annotation.value()).thenReturn(10);

        minLengthConstraint.configure(annotation);
        assertEquals(10, minLengthConstraint.getMin());
    }

    @Test
    public void testValidate_WithNullValue() {
        assertTrue(MinLengthConstraint.validate(null, 1));
    }

    @Test
    public void testIsValid() {
        int min = 3;
        MinLengthConstraint minLengthConstraint = new MinLengthConstraint(min);
        assertTrue(minLengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(min)));
        assertTrue(minLengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(min)));
        assertFalse(minLengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(min - 1)));
    }
}
