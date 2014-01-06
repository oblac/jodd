// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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