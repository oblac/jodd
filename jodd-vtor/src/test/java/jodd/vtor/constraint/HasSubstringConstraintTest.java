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

public class HasSubstringConstraintTest extends ConstraintTestBase {

    @Test
    public void testConstructor1() {
        String someStr = "someStr";
        HasSubstringConstraint hasSubstringConstraint = new HasSubstringConstraint(someStr, true);
        assertEquals(someStr, hasSubstringConstraint.getSubstring());
        assertTrue(hasSubstringConstraint.isIgnoreCase());
    }

    @Test
    public void testConstructor2() {
        HasSubstringConstraint hasSubstringConstraint = new HasSubstringConstraint();
        assertNull(hasSubstringConstraint.getSubstring());
        assertFalse(hasSubstringConstraint.isIgnoreCase());
    }

    @Test
    public void testConfigure() {
        HasSubstringConstraint hasSubstringConstraint = new HasSubstringConstraint();
        HasSubstring annotation = mock(HasSubstring.class);
        String substring = "testString";
        boolean ignoreCase = true;
        when(annotation.value()).thenReturn(substring);
        when(annotation.ignoreCase()).thenReturn(ignoreCase);

        hasSubstringConstraint.configure(annotation);
        assertEquals(substring, hasSubstringConstraint.getSubstring());

        assertEquals(ignoreCase, hasSubstringConstraint.isIgnoreCase());
    }

    @Test
    public void testSetSubstring() {
        HasSubstringConstraint hasSubstringConstraint = new HasSubstringConstraint();
        String someStr = "someStr";
        hasSubstringConstraint.setSubstring(someStr);
        assertEquals(someStr, hasSubstringConstraint.getSubstring());
    }

    @Test
    public void testSetIgnoreCase() {
        HasSubstringConstraint hasSubstringConstraint = new HasSubstringConstraint();
        hasSubstringConstraint.setIgnoreCase(true);
        assertTrue(hasSubstringConstraint.isIgnoreCase());
    }

    @Test
    public void testValidate_WithNullValue() {
        HasSubstringConstraint hasSubstringConstraint = new HasSubstringConstraint();
        assertTrue(hasSubstringConstraint.isValid(mockContext(), null));
    }

    @Test
    public void testIgnoreCase_False() {
        HasSubstringConstraint hasSubstringConstraint = new HasSubstringConstraint();
        hasSubstringConstraint.setSubstring("al");
        hasSubstringConstraint.setIgnoreCase(false);

        assertTrue(hasSubstringConstraint.isValid(mockContext(), "value"));
        assertFalse(hasSubstringConstraint.isValid(mockContext(), "VALUE"));
        assertFalse(hasSubstringConstraint.isValid(mockContext(), "FOO"));
    }

    @Test
    public void testIgnoreCase_True() {
        HasSubstringConstraint hasSubstringConstraint = new HasSubstringConstraint();
        hasSubstringConstraint.setSubstring("al");
        hasSubstringConstraint.setIgnoreCase(true);

        assertTrue(hasSubstringConstraint.isValid(mockContext(), "value"), "result mast be true when validate low case string");
        assertTrue(hasSubstringConstraint.isValid(mockContext(), "VALUE"), "result mast be true when validate upper case string");
        assertFalse(hasSubstringConstraint.isValid(mockContext(), "FOO"), "result must be false when validate string without substring");
    }
}
