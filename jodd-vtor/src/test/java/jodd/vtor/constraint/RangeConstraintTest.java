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

public class RangeConstraintTest extends ConstraintTestBase {

    @Test
    public void testConstructor1() {
        RangeConstraint rangeConstraint = new RangeConstraint();
        assertEquals("value must be default", rangeConstraint.getMin(), 0.0, 0.01);
        assertEquals("value must be default", rangeConstraint.getMax(), 0.0, 0.01);
    }

    @Test
    public void testConstructor2() {
        RangeConstraint rangeConstraint = new RangeConstraint(1.1, 10.1);
        assertEquals("min value must be the same as was given to constructor", rangeConstraint.getMin(), 1.1, 0.01);
        assertEquals("max value must be the same as was given to constructor", rangeConstraint.getMax(), 10.1, 0.01);
    }


    @Test
    public void testSetMinMax() {
        RangeConstraint rangeConstraint = new RangeConstraint();
        rangeConstraint.setMin(1.1);
        rangeConstraint.setMax(10.1);
        assertEquals("method must return the same value as was given to set method", rangeConstraint.getMin(), 1.1, 0.01);
        assertEquals("method must return the same value as was given to set method", rangeConstraint.getMax(), 10.1, 0.01);
    }

    @Test
    public void testConfigure() {
        RangeConstraint rangeConstraint = new RangeConstraint();
        Range annotation = mock(Range.class);
        stub(annotation.min()).toReturn(1.1);
        stub(annotation.max()).toReturn(10.1);

        rangeConstraint.configure(annotation);
        assertEquals("method must return the same value as was set to annotation when configure", rangeConstraint.getMin(), 1.1, 0.01);
        assertEquals("method must return the same value as was set to annotation when configure", rangeConstraint.getMax(), 10.1, 0.01);
    }

    @Test
    public void testValidate_WithValIsNull() {
        assertTrue("result must be true when validate null value", RangeConstraint.validate(null, 1, 2));
    }

    @Test
    public void testIsValid() {
        assertFalse("result must be false when validate value less than min", new RangeConstraint(1.1, 2.0).isValid(mockContext(), "1.0"));
        assertFalse("result must be false when validate value grater than max", new RangeConstraint(1.1, 3.0).isValid(mockContext(), "3.1"));
        assertTrue("result must be true when validate value grater than min and less than max", new RangeConstraint(2.0, 3.0).isValid(mockContext(), "2.8"));
        assertTrue("result must be true when validate value equal to min", new RangeConstraint(2.1, 3.0).isValid(mockContext(), "2.1"));
        assertTrue("result must be true when validate value equal to max", new RangeConstraint(1.0, 2.1).isValid(mockContext(), "2.1"));
    }
}