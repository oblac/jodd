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

class WildcardPathMatchConstraintTest extends ConstraintTestBase {

    @Test
    void testConstructor1() {
        WildcardPathMatchConstraint wildcardPathMatchConstraint = new WildcardPathMatchConstraint();
        assertNull(wildcardPathMatchConstraint.getPattern());
    }

    @Test
    void testConstructor2() {
        WildcardPathMatchConstraint wildcardPathMatchConstraint = new WildcardPathMatchConstraint("foo");
        assertEquals(wildcardPathMatchConstraint.getPattern(), "foo");
    }

    @Test
    void testSetPattern() {
        WildcardPathMatchConstraint wildcardPathMatchConstraint = new WildcardPathMatchConstraint();
        String pattern = "foo";
        wildcardPathMatchConstraint.setPattern(pattern);

        assertEquals(pattern, wildcardPathMatchConstraint.getPattern());
    }

    @Test
    void testConfigure() {
        WildcardPathMatchConstraint wildcardPathMatchConstraint = new WildcardPathMatchConstraint();
        WildcardPathMatch annotation = mock(WildcardPathMatch.class);
        String pattern = "foo";
        when(annotation.value()).thenReturn(pattern);

        wildcardPathMatchConstraint.configure(annotation);
        assertEquals(pattern, wildcardPathMatchConstraint.getPattern());
    }

    @Test
    void testValidate_WithValIsNull() {
        assertTrue(WildcardPathMatchConstraint.validate(null, "*"));
    }

    @Test
    void testIsValid() {
        assertTrue(new WildcardPathMatchConstraint("/dir/**").isValid(mockContext(), "/dir/abc"));
        assertFalse(new WildcardPathMatchConstraint("/dir/abz").isValid(mockContext(), "/dir/abc"));
    }
}
