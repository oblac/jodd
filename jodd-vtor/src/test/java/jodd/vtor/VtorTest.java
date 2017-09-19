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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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
        assertFalse(vtor.hasViolations(), "method must return false when violations list is empty");

        vtor.addViolation(mock(Violation.class));
        assertTrue(vtor.hasViolations(), "method must return true when add some violation");
    }

    @Test
    public void testAddViolation_withNullValue() throws Exception {
        Vtor vtor = new Vtor();
        vtor.addViolation(null);
        assertNull(vtor.getViolations());
    }

    @Test
    public void testAddViolation_withTwoDifferentValues() throws Exception {
        //given
        Vtor vtor = new Vtor();
        Violation violation1 = mock(Violation.class);
        vtor.addViolation(violation1);
        Violation violation2 = mock(Violation.class);
        vtor.addViolation(violation2);
        assertEquals(2, vtor.getViolations().size());
        assertEquals(violation1, vtor.getViolations().get(0));
        assertEquals(violation2, vtor.getViolations().get(1));

        //when
        vtor.addViolation(null);

        //then
        assertEquals(2, vtor.getViolations().size());
    }

    @Test
    public void testUseProfile_withNullValue() throws Exception {
        Vtor vtor = new Vtor();
        vtor.useProfile(null);
        assertNull(vtor.enabledProfiles);
    }

    @Test
    public void testUseProfile_withTwoDifferentValues() throws Exception {
        //given
        Vtor vtor = new Vtor();
        vtor.useProfile("testProfile");
        assertEquals(1, vtor.enabledProfiles.size());
        assertTrue(new ArrayList<>(vtor.enabledProfiles).contains("testProfile"), "first element of enabled profiles must be testProfile when use testProfile");

        //when
        vtor.useProfile(null);

        //then
        assertEquals(1, vtor.enabledProfiles.size());
    }

    @Test
    public void testUseProfiles_withNullValue() throws Exception {
        Vtor vtor = new Vtor();
        vtor.useProfiles(null);
        assertNull(vtor.enabledProfiles);
    }

    @Test
    public void testUseProfiles_withTwoDifferentValues() throws Exception {
        //given
        Vtor vtor = new Vtor();
        vtor.useProfiles("testProfile1", "testProfile2");
        assertEquals(2, vtor.enabledProfiles.size());
        ArrayList<String> enabledProfileList = new ArrayList<>(vtor.enabledProfiles);
        assertTrue(enabledProfileList.contains("testProfile1"), "first element must be equal to first added profile");
        assertTrue(enabledProfileList.contains("testProfile2"), "second element must be equal to second added profile");

        //when
        vtor.useProfile(null);

        //then
        assertEquals(2, vtor.enabledProfiles.size());
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
        when(testCheck1Constraint.isValid(any(ValidationConstraintContext.class), any())).thenReturn(false);
        Map<String, List<Check>> constraints = createValidateFldHasCheckerTestConstraints(testCheck1Constraint);

        //when validate an object with field testField
        List<Violation> violations = new Vtor().validate(mockValidationContext(constraints), new ValidateTestObject("testValue"), "testField");

        //then
        //isValid for ValidationConstraint mast be invoked
        verify(testCheck1Constraint).isValid(any(ValidationConstraintContext.class), eq("testValue"));
        assertEquals(1, violations.size());
    }

    @Test
    public void testValidateFldHasChecker_isValidTrue() throws Exception {
        //given
        ValidationConstraint testCheck1Constraint = mock(ValidationConstraint.class);
        //ValidationConstraint.isValid always returns false
        when(testCheck1Constraint.isValid(any(ValidationConstraintContext.class), any())).thenReturn(true);
        Map<String, List<Check>> constraints = createValidateFldHasCheckerTestConstraints(testCheck1Constraint);

        //when validate an object with field testField
        List<Violation> validate = new Vtor().validate(mockValidationContext(constraints), new ValidateTestObject("testValue"), "testField");

        //then
        //isValid for ValidationConstraint mast be invoked
        verify(testCheck1Constraint).isValid(any(ValidationConstraintContext.class), eq("testValue"));
        assertNull(validate);
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
        assertEquals(1, violations.size());
        assertEquals("profil1", violations.get(0).getCheck().getProfiles()[0]);
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
        assertEquals(1, violations.size());
        assertEquals(ch1, violations.get(0).getCheck());
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
