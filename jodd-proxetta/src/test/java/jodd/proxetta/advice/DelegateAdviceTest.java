// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.advice;

import jodd.proxetta.data.Calc;
import jodd.proxetta.data.CalcImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class DelegateAdviceTest {

	@Test
	public void testCalcImplDelegate() {
		CalcImpl calc = new CalcImpl();

		Calc newCalc = DelegateAdviceUtil.applyAdvice(CalcImpl.class);
		DelegateAdviceUtil.injectTargetIntoProxy(newCalc, calc);

		assertNotEquals(newCalc.getClass(), calc.getClass());

		assertEquals(calc.calculate(2, 8), newCalc.calculate(2, 8));

		try {
			newCalc.hello();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.toString());
		}
	}

}