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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VtorMatchProfilesWithResetedProfilesTest {
    private Vtor vtor;

    @BeforeEach
    public void setup() {
        //given
        vtor = new Vtor();
        vtor.resetProfiles();
    }

    @Test
    public void testValidateAllProfilesByDefaultIsTrue() {
        vtor.setValidateAllProfilesByDefault(true);
        assertTrue(vtor.matchProfiles(new String[]{"testProfile"}));
    }

    @Test
    public void testNullProfiles() {
        assertTrue(vtor.matchProfiles(null));
    }

    @Test
    public void testEmptyListOfProfiles() {
        assertTrue(vtor.matchProfiles(new String[]{}));
    }

    @Test
    public void testOneProfileIsEmpty() {
        assertTrue(vtor.matchProfiles(new String[]{"", "testProfile"}));
    }

    @Test
    public void testOneProfileIsDefault() {
        assertTrue(vtor.matchProfiles(new String[]{Vtor.DEFAULT_PROFILE, "testProfile"}));
    }

    @Test
    public void testNonSpecialProfiles(){
        assertFalse(vtor.matchProfiles(new String[]{"testProfile1", "testProfile2"}));
    }

}
