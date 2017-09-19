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

public class WildcardMatchConstraintTest extends ConstraintTestBase {

    @Test
    public void testConstructor1() {
        WildcardMatchConstraint wildcardMatchConstraint = new WildcardMatchConstraint();
        assertNull(wildcardMatchConstraint.getPattern());
    }

    @Test
    public void testConstructor2() {
        String pattern = "foo";
        WildcardMatchConstraint wildcardMatchConstraint = new WildcardMatchConstraint(pattern);
        assertEquals(pattern, wildcardMatchConstraint.getPattern());
    }

    @Test
    public void testSetPattern() {
        WildcardMatchConstraint wildcardMatchConstraint = new WildcardMatchConstraint();
        String pattern = "foo";
        wildcardMatchConstraint.setPattern(pattern);
        assertEquals(pattern, wildcardMatchConstraint.getPattern());
    }

    @Test
    public void testConfigure() {
        WildcardMatchConstraint wildcardMatchConstraint = new WildcardMatchConstraint();
        WildcardMatch annotation = mock(WildcardMatch.class);
        String pattern = "foo";
        when(annotation.value()).thenReturn(pattern);
        wildcardMatchConstraint.configure(annotation);
        assertEquals(pattern, wildcardMatchConstraint.getPattern());
    }

    @Test
    public void testValidate_WithValIsNull() {
        assertTrue(WildcardMatchConstraint.validate(null, "*"));
    }

    @Test
    public void testIsValid() {
        assertTrue(new WildcardMatchConstraint("a?c").isValid(mockContext(), "abc"));
        assertFalse(new WildcardMatchConstraint("axc").isValid(mockContext(), "abc"));
    }
}
