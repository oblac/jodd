package jodd.vtor;

import jodd.vtor.data.Woo;
import junit.framework.TestCase;

import java.util.List;

public class AnnotationTest extends TestCase {

	public void testAnn() {
		Vtor vtor = new Vtor();
		vtor.validate(new Woo());
		List<Violation> v = vtor.getViolations();
		assertEquals(1, v.size());

		vtor.resetViolations();
		vtor.useProfiles("p1", "p2");
		vtor.validate(new Woo());
		v = vtor.getViolations();
		assertEquals(2, v.size());

		vtor.resetViolations();
		vtor.useProfiles("default", "p1", "p2");
		vtor.validate(new Woo());
		v = vtor.getViolations();
		assertEquals(3, v.size());
	}
}
