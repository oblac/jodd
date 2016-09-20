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

package jodd.vtor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VtorExceptionTest {

    @Test
    public void testConstructor1() {
        //given
        RuntimeException cause = new RuntimeException("test");
        //when
        VtorException vtorException = new VtorException(cause);
        //then
        assertEquals("created instance must have the same cause as was given to its constructor", vtorException.getCause(), cause);
    }

    @Test
    public void testConstructor2() {
        //given
        String message = "test";
        //when
        VtorException vtorException = new VtorException(message);
        //then
        assertEquals("created instance must have the same message as was given to its constructor", vtorException.getMessage(), message);
    }

    @Test
    public void testConstructor3() {
        //given
        String message = "test";
        RuntimeException cause = new RuntimeException();
        //when
        VtorException vtorException = new VtorException(message, cause);
        //then
        assertEquals("created instance must return message with cause details when create instance with message and some cause", vtorException.getMessage(), message+"; <--- java.lang.RuntimeException");
        assertEquals("created instance must have the same cause as was given to its constructor", vtorException.getCause(), cause);
    }
}