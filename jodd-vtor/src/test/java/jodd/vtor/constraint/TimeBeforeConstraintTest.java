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
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimeBeforeConstraintTest extends ConstraintTestBase {

    @Test
    public void testConstructor1() {
        TimeBeforeConstraint timeBeforeConstraint = new TimeBeforeConstraint();
        assertNull("time value must be null by default", timeBeforeConstraint.getTime());
    }

    @Test
    public void testConstructor2() {
        JDateTime time = new JDateTime();
        TimeBeforeConstraint timeBeforeConstraint = new TimeBeforeConstraint(time);
        assertEquals("time must be the same as was given to constructor", timeBeforeConstraint.getTime(), time);
    }

    @Test
    public void testSetTime() {
        TimeBeforeConstraint timeBeforeConstraint = new TimeBeforeConstraint();
        JDateTime time = new JDateTime();
        timeBeforeConstraint.setTime(time);
        assertEquals("method must return the same time as was given to set method", timeBeforeConstraint.getTime(), time);
    }

    @Test
    public void testConfigure() {
        TimeBeforeConstraint timeBeforeConstraint = new TimeBeforeConstraint();
        JDateTime time = new JDateTime();
        timeBeforeConstraint.setTime(time);
        TimeBefore annotation = mock(TimeBefore.class);
        when(annotation.value()).thenReturn(JDateTimeDefault.formatter.convert(time, JDateTimeDefault.format));

        timeBeforeConstraint.configure(annotation);
        assertEquals("method must return the same time as was set to annotation when configure", timeBeforeConstraint.getTime(), time);
    }

    @Test
    public void testValidate_WithValIsNull() {
        assertTrue("result must be true when validate null value", TimeBeforeConstraint.validate(null, new JDateTime("2011-05-01 10:11:12.344")));
    }

    @Test
    public void testIsValid() {
        JDateTime time = new JDateTime("2011-05-01 10:11:12.344");
        TimeBeforeConstraint constraint = new TimeBeforeConstraint(time.clone());

        assertFalse("result must be true when validate time which is equal to constraint time", constraint.isValid(mockContext(), time.clone()));
        assertTrue("result must be false when validate time which is less than constraint time", constraint.isValid(mockContext(), time.clone().subMinute(1)));
        assertFalse("result must be true when validate time which is greater than constraint time", constraint.isValid(mockContext(), time.clone().addMinute(1)));
    }
}