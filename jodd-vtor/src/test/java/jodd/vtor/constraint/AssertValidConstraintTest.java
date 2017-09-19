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

import jodd.vtor.ValidationConstraintContext;
import jodd.vtor.ValidationContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

public class AssertValidConstraintTest extends ConstraintTestBase {

    @Test
    public void testIsValid_withNullValue() {
        //given
        ValidationContext targetValidationContext = mock(ValidationContext.class);
        AssertValidConstraint assertValidConstraint = new AssertValidConstraint(targetValidationContext);
        ValidationConstraintContext vcc = mockContext();

        //when
        boolean valid = assertValidConstraint.isValid(vcc, null);

        //then
        assertTrue(valid);
        verify(vcc, never()).validateWithin(eq(targetValidationContext), isNull());
    }

    @Test
    public void testIsValid() {
        //given
        ValidationContext targetValidationContext = mock(ValidationContext.class);
        AssertValidConstraint assertValidConstraint = new AssertValidConstraint(targetValidationContext);
        ValidationConstraintContext vcc = mockContext();
        Object someValue = new Object();
        //this method is empty so nothing to check
        assertValidConstraint.configure(null);

        //when validate some value
        boolean valid = assertValidConstraint.isValid(vcc, someValue);

        //then validateWithin must be called for validated value
        assertTrue(valid);
        //validateWithin must be called for validated value
        verify(vcc).validateWithin(eq(targetValidationContext), eq(someValue));
    }
}
