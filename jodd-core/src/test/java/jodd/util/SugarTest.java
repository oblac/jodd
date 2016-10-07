package jodd.util;

import org.junit.Assert;
import org.junit.Test;

import static jodd.util.Sugar.arr;

public class SugarTest {

	@Test
	public void testArr() {
		String[] strings = arr("one", "two");
		Assert.assertArrayEquals(new String[] {"one", "two"}, strings);
	}
}
