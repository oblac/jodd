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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SizeConstraintTest extends ConstraintTestBase {

    @Test
    public void testConstructor1() {
        SizeConstraint sizeConstraint = new SizeConstraint();
        assertEquals(0, sizeConstraint.getMin());
        assertEquals(0, sizeConstraint.getMax());
    }

    @Test
    public void testConstructor2() {
        SizeConstraint sizeConstraint = new SizeConstraint(10, 20);
        assertEquals(10, sizeConstraint.getMin());
        assertEquals(20, sizeConstraint.getMax());
    }

    @Test
    public void testSetMinMax() {
        SizeConstraint sizeConstraint = new SizeConstraint();
        sizeConstraint.setMin(10);
        sizeConstraint.setMax(20);
        assertEquals(10, sizeConstraint.getMin());
        assertEquals(20, sizeConstraint.getMax());
    }

    @Test
    public void testConfigure() {
        SizeConstraint sizeConstraint = new SizeConstraint();
        Size annotation = mock(Size.class);
        when(annotation.min()).thenReturn(10);
        when(annotation.max()).thenReturn(20);

        sizeConstraint.configure(annotation);
        assertEquals(10, sizeConstraint.getMin());
        assertEquals(20, sizeConstraint.getMax());
    }

    @Test
    public void testValidate_WithValIsNull() {
        assertTrue(SizeConstraint.validate(null, 1, 2));
    }

    private void sizeConstraintCheck(Object val) {
        assertFalse(new SizeConstraint(4, 5).isValid(mockContext(), val));
        assertFalse(new SizeConstraint(0, 1).isValid(mockContext(), val));
        assertTrue(new SizeConstraint(1, 3).isValid(mockContext(), val));
        assertTrue(new SizeConstraint(1, 2).isValid(mockContext(), val));
    }

    @Test
    public void testIsValid_ForCollection() {
        List<String> val = new ArrayList<>();
        val.add("1");
        val.add("2");
        sizeConstraintCheck(val);
    }

    @Test
    public void testIsValid_ForMap() {
        Map<String, String> val = new HashMap<>();
        val.put("1", "one");
        val.put("2", "two");
        sizeConstraintCheck(val);
    }

    @Test
    public void testIsValid_ForArray() {
        sizeConstraintCheck(new String[]{"one", "two"});
    }

    @Test
    public void testValidate_ForUnknownClass() {
        assertFalse(SizeConstraint.validate(new Object(), 0, 1));
    }
}
