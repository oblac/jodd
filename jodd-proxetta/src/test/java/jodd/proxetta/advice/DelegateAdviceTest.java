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

package jodd.proxetta.advice;

import jodd.proxetta.fixtures.data.Calc;
import jodd.proxetta.fixtures.data.CalcImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DelegateAdviceTest {

	@Test
	public void testCalcImplDelegate() {
		CalcImpl calc = new CalcImpl();

		Calc newCalc = DelegateAdviceUtil.applyAdvice(CalcImpl.class);
		DelegateAdviceUtil.injectTargetIntoProxy(newCalc, calc);

		assertNotEquals(newCalc.getClass(), calc.getClass());

		assertEquals(calc.calculate(2, 8), newCalc.calculate(2, 8));
		assertEquals(calc.calculate(2L, 8L), newCalc.calculate(2L, 8L));
		assertEquals(calc.calculate(2.5d, 8.5d), newCalc.calculate(2.5d, 8.5d), 0.1);
		assertEquals(calc.calculate(2.5f, 8.5f), newCalc.calculate(2.5f, 8.5f), 0.1);
		assertEquals(calc.calculate((byte)2, (byte)8), newCalc.calculate((byte)2, (byte)8));
		assertEquals(calc.calculate((short)2, (short)8), newCalc.calculate((short)2, (short)8));

		try {
			newCalc.hello();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.toString());
		}
	}

}
