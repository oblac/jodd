package jodd.petite;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class SyntheticTest {

	public static class BeanA {
		public void test() {
			final Map map = new HashMap();
			new Thread(() -> System.out.print(map));
		}
	}

	@Test
	void synthTest() {
		final PetiteContainer pc = new PetiteContainer();
		pc.addBean("foo", new BeanA());
		pc.getBean("foo");
	}
}
