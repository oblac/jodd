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

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

public class VtorTest extends VtorTestSupport {

    @Test
    public void testCreate() throws Exception {
        Vtor vtor = Vtor.create();
        assertNotNull(vtor);
    }

    @Test
    public void testSetSeverity() throws Exception {
        Vtor vtor = new Vtor();
        vtor.setSeverity(1);
        assertEquals(vtor.severity, 1);
    }

    @Test
    public void testValidateAllProfilesByDefault() throws Exception {
        Vtor vtor = new Vtor();
        vtor.setValidateAllProfilesByDefault(true);
        assertTrue(vtor.isValidateAllProfilesByDefault());
    }

    @Test
    public void testHasViolations() throws Exception {
        Vtor vtor = new Vtor();
        assertFalse("method must return false when violations list is empty", vtor.hasViolations());

        vtor.addViolation(mock(Violation.class));
        assertTrue("method must return true when add some violation", vtor.hasViolations());
    }

    @Test
    public void testAddViolation_withNullValue() throws Exception {
        Vtor vtor = new Vtor();
        vtor.addViolation(null);
        assertNull("list of violations must be null when add only null violation", vtor.getViolations());
    }

    @Test
    public void testAddViolation_withTwoDifferentValues() throws Exception {
        //given
        Vtor vtor = new Vtor();
        Violation violation1 = mock(Violation.class);
        vtor.addViolation(violation1);
        Violation violation2 = mock(Violation.class);
        vtor.addViolation(violation2);
        assertEquals("size of list with violations must be 2 when add two violation", vtor.getViolations().size(), 2);
        assertEquals("first violation must be equal to first added violation", vtor.getViolations().get(0), violation1);
        assertEquals("second violation must be equal to second added violation", vtor.getViolations().get(1), violation2);

        //when
        vtor.addViolation(null);

        //then
        assertEquals("list of violations must not be changed when add a null violation", vtor.getViolations().size(), 2);
    }

    @Test
    public void testUseProfile_withNullValue() throws Exception {
        Vtor vtor = new Vtor();
        vtor.useProfile(null);
        assertNull("list of enabled profiles must be null when add only null profile", vtor.enabledProfiles);
    }

    @Test
    public void testUseProfile_withTwoDifferentValues() throws Exception {
        //given
        Vtor vtor = new Vtor();
        vtor.useProfile("testProfile");
        assertEquals("size of list with profiles must be 1 when add one profile", vtor.enabledProfiles.size(), 1);
        assertEquals("first element of enabled profiles must be testProfile when use testProfile", vtor.enabledProfiles.iterator().next(), "testProfile");

        //when
        vtor.useProfile(null);

        //then
        assertEquals("size of list with profiles must not be changed when use null profile", vtor.enabledProfiles.size(), 1);
    }

    @Test
    public void testUseProfiles_withNullValue() throws Exception {
        Vtor vtor = new Vtor();
        vtor.useProfiles((String) null);
        assertNull("list of enabled profiles must be null when add only null profile", vtor.enabledProfiles);
    }

    @Test
    @Ignore
    public void testUseProfiles_withTwoDifferentValues() throws Exception {
        //given
        Vtor vtor = new Vtor();
        vtor.useProfiles("testProfile1", "testProfile2");
        assertEquals("size of list with profiles must be 2 when add two profile", vtor.enabledProfiles.size(), 2);
        Iterator<String> resultIterator = vtor.enabledProfiles.iterator();
        assertEquals("first element must be equal to first added profile", resultIterator.next(), "testProfile1");
        assertEquals("second element must be equal to second added profile", resultIterator.next(), "testProfile2");

        //when
        vtor.useProfile(null);

        //then
        assertEquals("size of list with profiles must not be changed when add null profile", vtor.enabledProfiles.size(), 2);
    }

    private Map<String, List<Check>> createValidateFldHasCheckerTestConstraints(ValidationConstraint testCheck1Constraint) {
        Map<String, List<Check>> constraints = new HashMap<>();
        List<Check> checks = new ArrayList<>();
        checks.add(new Check("testCheck1", testCheck1Constraint));
        constraints.put("testField", checks);
        return constraints;
    }

    @Test
    public void testValidateFldHasChecker_isValidFalse() throws Exception {
        //given
        ValidationConstraint testCheck1Constraint = mock(ValidationConstraint.class);
        //ValidationConstraint.isValid always returns false
        stub(testCheck1Constraint.isValid(any(ValidationConstraintContext.class), any())).toReturn(false);
        Map<String, List<Check>> constraints = createValidateFldHasCheckerTestConstraints(testCheck1Constraint);

        //when validate an object with field testField
        List<Violation> violations = new Vtor().validate(mockValidationContext(constraints), new ValidateTestObject("testValue"), "testField");

        //then
        //isValid for ValidationConstraint mast be invoked
        verify(testCheck1Constraint).isValid(any(ValidationConstraintContext.class), eq("testValue"));
        assertEquals("result must contain one violation when constraint check returns false", violations.size(), 1);
    }

    @Test
    public void testValidateFldHasChecker_isValidTrue() throws Exception {
        //given
        ValidationConstraint testCheck1Constraint = mock(ValidationConstraint.class);
        //ValidationConstraint.isValid always returns false
        stub(testCheck1Constraint.isValid(any(ValidationConstraintContext.class), any())).toReturn(true);
        Map<String, List<Check>> constraints = createValidateFldHasCheckerTestConstraints(testCheck1Constraint);

        //when validate an object with field testField
        List<Violation> validate = new Vtor().validate(mockValidationContext(constraints), new ValidateTestObject("testValue"), "testField");

        //then
        //isValid for ValidationConstraint mast be invoked
        verify(testCheck1Constraint).isValid(any(ValidationConstraintContext.class), eq("testValue"));
        assertNull("result must not contain any violation when constraint check returns true", validate);
    }

    @Test
    public void testValidateCheckForDifferentProfile() throws Exception {
        //given a list of constraints with different profiles, one check has same profile as Vtor
        Vtor vtor = new Vtor();
        vtor.useProfile("profil1");
        Map<String, List<Check>> constraints = new HashMap<>();
        ValidationConstraint testCheck1Constraint = mock(ValidationConstraint.class);
        ValidationConstraint testCheck2Constraint = mock(ValidationConstraint.class);
        Check ch1 = createCheckWithProfile("check1", "profil1", testCheck1Constraint);
        Check ch2 = createCheckWithProfile("check2", "profil2", testCheck2Constraint);
        List<Check> checks = new ArrayList<>();
        checks.add(ch1);
        checks.add(ch2);
        constraints.put("testField", checks);

        //when
        List<Violation> violations = vtor.validate(mockValidationContext(constraints), new ValidateTestObject("testValue"), "testField");

        //then
        assertEquals("result must contain one violation", violations.size(), 1);
        assertEquals("result must contain one violation with check for profile1", violations.get(0).getCheck().getProfiles()[0], "profil1");
        verify(testCheck1Constraint).isValid(any(ValidationConstraintContext.class), eq("testValue"));
        verify(testCheck2Constraint, never()).isValid(any(ValidationConstraintContext.class), eq("testValue"));
    }

    @Test
    public void testValidateCheckSeverity() throws Exception {
        //given
        Vtor vtor = new Vtor();
        vtor.setSeverity(10);

        Map<String, List<Check>> constraints = new HashMap<>();
        ValidationConstraint testCheck1Constraint = mock(ValidationConstraint.class);
        ValidationConstraint testCheck2Constraint = mock(ValidationConstraint.class);

        Check ch1 = new Check("check1", testCheck1Constraint);
        ch1.setSeverity(15);
        Check ch2 = new Check("check2", testCheck2Constraint);
        ch2.setSeverity(5);
        List<Check> checks = new ArrayList<>();
        checks.add(ch1);
        checks.add(ch2);
        constraints.put("testField", checks);

        //when
        List<Violation> violations = vtor.validate(mockValidationContext(constraints), new ValidateTestObject("testValue"), "testField");

        //then
        assertEquals("list of violations must have size 1 when validate two checks with severity 5 and 15", violations.size(), 1);
        assertEquals("list of violations must contain check1 with severity 15 when validate two checks with severity 5 and 15", violations.get(0).getCheck(), ch1);
        verify(testCheck1Constraint).isValid(any(ValidationConstraintContext.class), eq("testValue"));
        verify(testCheck2Constraint, never()).isValid(any(ValidationConstraintContext.class), eq("testValue"));
    }

    private static class ValidateTestObject {
        private final String testField;

        public ValidateTestObject(String testField) {
            this.testField = testField;
        }

        public String getTestField() {
            return testField;
        }
    }


}
