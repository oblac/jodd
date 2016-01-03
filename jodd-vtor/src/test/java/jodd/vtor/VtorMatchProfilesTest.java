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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class VtorMatchProfilesTest {
    private Vtor vtor;

    @Before
    public void setup() {
        vtor = new Vtor();
    }

    @Test
    public void testAllProfiles() {
        assertTrue("result must be true when match a list with one element which is *", vtor.matchProfiles(new String[]{Vtor.ALL_PROFILES}));
        assertFalse("result must be false when match a list with many elements", vtor.matchProfiles(new String[]{Vtor.ALL_PROFILES, "someProfile"}));
    }

    @Test
    public void testMatchProfilesAgainstSomeProfile() {
        //when
        vtor.useProfile("testProfile1");

        //then
        assertFalse("result must be false when match null value",
                vtor.matchProfiles(null));

        assertFalse("result must be false when match list of profiles without any assigned profiles",
                vtor.matchProfiles(new String[]{"testProfile2", "testProfile3"}));

        assertTrue("result must be true when match list of profiles with one assigned profile",
                vtor.matchProfiles(new String[]{"testProfile1", "testProfile2", "testProfile3"}));

        assertTrue("result must be true when match list of profiles with one assigned profile",
                vtor.matchProfiles(new String[]{"testProfile1", "testProfile2", "testProfile3"}));

        assertTrue("result must be true when match list of profiles with one assigned profile",
                vtor.matchProfiles(new String[]{"testProfile1", "testProfile2", "testProfile3"}));

        assertTrue("result must be true when match list of profiles with one assigned profile",
                vtor.matchProfiles(new String[]{"testProfile1", "testProfile2", "testProfile3"}));

        assertTrue("result must be true when match unordered list of profiles with one assigned profile",
                vtor.matchProfiles(new String[]{"testProfile2", "testProfile1", "testProfile3"}));

        assertFalse("result must be false when match a list of profiles with one wrong mandatory profile",
                vtor.matchProfiles(new String[]{"+testProfile2", "testProfile1", "testProfile3"}));

        assertFalse("result must be false when match a list of profiles with one assigned profile which was marked as optional",
                vtor.matchProfiles(new String[]{"testProfile2", "-testProfile1", "testProfile3"}));
    }

    @Test
    public void testDefaultProfile() {
        //when
        vtor.useProfile(Vtor.DEFAULT_PROFILE);

        //then
        assertTrue("result must be true when match null value", vtor.matchProfiles(null));
        assertTrue("result must be true when match a list with empty profile", vtor.matchProfiles(new String[]{"testProfile2", "", "testProfile3"}));
        assertFalse("result must be false when match a list without empty profile", vtor.matchProfiles(new String[]{"testProfile2", "testProfile3"}));
    }

}

