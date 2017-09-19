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

import jodd.datetime.JDateTime;
import jodd.datetime.JDateTimeDefault;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimeAfterConstraintTest extends ConstraintTestBase {

    @Test
    public void testConstructor1() {
        TimeAfterConstraint timeAfterConstraint = new TimeAfterConstraint();
        assertNull(timeAfterConstraint.getTime());
    }

    @Test
    public void testConstructor2() {
        JDateTime time = new JDateTime();
        TimeAfterConstraint timeAfterConstraint = new TimeAfterConstraint(time);
        assertEquals(time, timeAfterConstraint.getTime());
    }

    @Test
    public void testSetTime() {
        TimeAfterConstraint timeAfterConstraint = new TimeAfterConstraint();
        JDateTime time = new JDateTime();
        timeAfterConstraint.setTime(time);
        assertEquals(time, timeAfterConstraint.getTime());
    }

    @Test
    public void testConfigure() {
        TimeAfterConstraint timeAfterConstraint = new TimeAfterConstraint();
        JDateTime time = new JDateTime();
        TimeAfter annotation = mock(TimeAfter.class);
        when(annotation.value()).thenReturn(JDateTimeDefault.formatter.convert(time, JDateTimeDefault.format));

        timeAfterConstraint.configure(annotation);
        assertEquals(time, timeAfterConstraint.getTime());
    }

    @Test
    public void testValidate_WithValIsNull() {
        assertTrue(TimeAfterConstraint.validate(null, new JDateTime("2011-05-01 10:11:12.344")));
    }

    @Test
    public void testIsValid() {
        JDateTime time = new JDateTime("2011-05-01 10:11:12.344");
        TimeAfterConstraint constraint = new TimeAfterConstraint(time.clone());

        assertFalse(constraint.isValid(mockContext(), time.clone()));
        assertFalse(constraint.isValid(mockContext(), time.clone().subMinute(1)));
        assertTrue(constraint.isValid(mockContext(), time.clone().addMinute(1)));
    }
}
