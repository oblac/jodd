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
import static org.mockito.Mockito.stub;

public class MaxLengthConstraintTest extends ConstraintTestBase {

    @Test
    public void testConstructor1() {
        MaxLengthConstraint maxLengthConstraint = new MaxLengthConstraint();
        assertEquals("value must be default", maxLengthConstraint.getMax(), 0);
    }

    @Test
    public void testConstructor2() {
        MaxLengthConstraint maxLengthConstraint = new MaxLengthConstraint(10);
        assertEquals("max value must be the same as was given to constructor", maxLengthConstraint.getMax(), 10);
    }

    @Test
    public void testSetMax() {
        MaxLengthConstraint maxLengthConstraint = new MaxLengthConstraint();
        int maxValue = 100;
        maxLengthConstraint.setMax(maxValue);
        assertEquals("method must return the same value as was given to set method", maxLengthConstraint.getMax(), maxValue);
    }

    @Test
    public void testConfigure() {
        MaxLengthConstraint maxLengthConstraint = new MaxLengthConstraint();
        MaxLength annotation = mock(MaxLength.class);
        int maxValue = 100;
        stub(annotation.value()).toReturn(maxValue);

        maxLengthConstraint.configure(annotation);
        assertEquals("method must return the same value as was set to annotation when configure", maxLengthConstraint.getMax(), maxValue);
    }

    @Test
    public void testValidate_WithValIsNull() {
        assertTrue("result must be true when validate null value", MaxLengthConstraint.validate(null, 1));
    }

    @Test
    public void testMaxLengthConstraint() {
        MaxLengthConstraint maxLengthConstraint = new MaxLengthConstraint(3);
        assertTrue("result must be true when validate a string with size less than maxValue", maxLengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(3)));
        assertFalse("result must be false when validate a string with size greater than maxValue", maxLengthConstraint.isValid(mockContext(), TestUtils.stringWithLength(4)));
    }
}