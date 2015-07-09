package jodd.bean;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import jodd.bean.data.FooBean5;
import jodd.bean.data.FooBean6;
import jodd.bean.data.Value;
import jodd.bean.data.ValueA;
import jodd.bean.data.ValueAA;
import jodd.bean.data.ValueB;
import jodd.bean.data.ValueBB;

import org.junit.Assert;
import org.junit.Test;

import sun.security.x509.NetscapeCertTypeExtension;

public class DeepBeanCopyTest {

	@Test
	public void testPrimitivesCopy() throws Exception {
		FooBean5 source = createFooBean5();
		FooBean5 destination = new FooBean5();
		
		DeepBeanCopy deepBeanCopy = DeepBeanCopy.beans(source, destination);
		
		deepBeanCopy.copy();
		
		Assert.assertEquals(source.getDefaultLocale(), destination.getDefaultLocale());
	}
	
	@Test
	public void testPrimitivesNullCopy() throws Exception {
		FooBean5 source = new FooBean5();
		FooBean5 destination = new FooBean5();
		
		DeepBeanCopy deepBeanCopy = DeepBeanCopy.beans(source, destination);
		
		deepBeanCopy.copy();
		
		Assert.assertEquals(null, destination.getDefaultLocale());
	}
	
	@Test
	public void testCollectionsCopy() throws Exception {
		FooBean5 source = new FooBean5();
		
		source.setLocales(
			createLocales(Locale.UK, Locale.CANADA, Locale.FRENCH));
		
		FooBean5 destination = new FooBean5();
		
		DeepBeanCopy deepBeanCopy = DeepBeanCopy.beans(source, destination);
		
		deepBeanCopy.copy();
		
		Assert.assertFalse(source.getLocales() == destination.getLocales());
		Assert.assertEquals(source.getLocales(), destination.getLocales());
	}
	
	@Test
	public void testMapSizeCopy() throws Exception {
		FooBean5 source = new FooBean5();
		
		Map<String, FooBean5> mapSource = new HashMap<>();
		
		mapSource.put("1", new FooBean5());
		mapSource.put("2", new FooBean5());
		mapSource.put("3", new FooBean5());
		
		source.setMap(mapSource);
		
		FooBean5 destination = new FooBean5();
		
		DeepBeanCopy deepBeanCopy = DeepBeanCopy.beans(source, destination);
		
		deepBeanCopy.copy();
		
		Assert.assertFalse(source.getMap() == destination.getMap());
		Assert.assertEquals(source.getMap().size(), destination.getMap().size());
	}
	
	@Test
	public void testMapCopy() throws Exception {
		FooBean5 source = new FooBean5();
		
		Map<String, FooBean5> mapSource = new HashMap<>();
		
		FooBean5 nestedFooBean5 = new FooBean5();
		
		nestedFooBean5.setDefaultLocale(Locale.ITALY);
		
		FooBean6 nestedFooBean6 = new FooBean6();
		
		nestedFooBean6.setName("name");
		
		nestedFooBean5.setFooBeans(Arrays.asList(nestedFooBean6));
		
		mapSource.put("1", nestedFooBean5);
		
		source.setMap(mapSource);
		
		FooBean5 destination = new FooBean5();
		
		DeepBeanCopy deepBeanCopy = DeepBeanCopy.beans(source, destination);
		
		deepBeanCopy.copy();
		
		Map<String, FooBean5> destinationMap = destination.getMap();
		
		FooBean5 actualNestedFooBean5 = destinationMap.get("1");
		
		Assert.assertEquals(nestedFooBean5.getDefaultLocale(), actualNestedFooBean5.getDefaultLocale());
		Assert.assertEquals(1, actualNestedFooBean5.getFooBeans().size());
		
		FooBean6 actualNestedFooBean6 = actualNestedFooBean5.getFooBeans().get(0);
		
		Assert.assertEquals(nestedFooBean6.getName(), actualNestedFooBean6.getName());
	}
	
	@Test
	public void testDeepWithIntefacesCopy() throws Exception {
		FooBean5 source = createFooBean5();
	
		List<FooBean6> fooBean6Beans = createFooBean6Beans(5, false);
		
		source.setFooBeans(fooBean6Beans);
		
		FooBean5 destination = new FooBean5();
		
		DeepBeanCopy deepBeanCopy = DeepBeanCopy.beans(source, destination);
		
		deepBeanCopy.addMappingType(ValueA.class, ValueAA.class);
		deepBeanCopy.addMappingType(ValueB.class, ValueBB.class);
		
		deepBeanCopy.copy();
		
		Assert.assertEquals(source.getDefaultLocale(), destination.getDefaultLocale());

		Assert.assertFalse(source.getLocales() == destination.getLocales());
		Assert.assertEquals(source.getLocales(), destination.getLocales());
		
		Assert.assertFalse(fooBean6Beans == destination.getFooBeans());
		assertEquals(fooBean6Beans, destination.getFooBeans());
	}
	
	private void assertEquals(List<FooBean6> expectedFooBeans, List<FooBean6> actualFooBeans) {
		for (int i = 0; i < expectedFooBeans.size(); i++) {
			FooBean6 expectedFooBean6 = expectedFooBeans.get(i);
			FooBean6 actualFooBean6 = actualFooBeans.get(i);
			
			Assert.assertEquals(expectedFooBean6.getName(), actualFooBean6.getName());
			Assert.assertEquals(expectedFooBean6.getValue().getString(), actualFooBean6.getValue().getString());
			
			if (expectedFooBean6.getValue() instanceof ValueA) {
				Assert.assertTrue(actualFooBean6.getValue() instanceof ValueAA);
			}
			else {
				Assert.assertTrue(actualFooBean6.getValue() instanceof ValueBB);
			}
		}
	}

	protected FooBean5 createFooBean5() {
		FooBean5 fooBean5 = new FooBean5();
		
		fooBean5.setDefaultLocale(Locale.US);
		fooBean5.setLocales(createLocales(Locale.US, Locale.GERMAN));
		
		return fooBean5;
	}
	
	protected FooBean6 createFooBean6() {
		FooBean6 fooBean6 = new FooBean6();
		
		fooBean6.setName(randomString());
		fooBean6.setValue(randomValue());
		
		return fooBean6;
	}
	
	protected int randomInt() {
		Random random = new Random();

		return new BigInteger(32, random).intValue();
	}
	
	protected String randomString() {
		return String.valueOf(randomInt());
	}
	
	protected Value randomValue() {
		Random random = new Random();

		if (random.nextInt(10) < 5) {
			return new ValueA(randomString());
		}
		
		return new ValueB(randomInt());
	}
	
	protected List<FooBean6> createFooBean6Beans(int size, boolean hasNested) {
		List<FooBean6> fooBean6Beans = new ArrayList<>(size);
		
		for (int i = 0; i < size; i++) {
			FooBean6 fooBean6 = createFooBean6();
		
			if (hasNested) {
				fooBean6.setNestedBeans(createFooBean6Beans(size, false));
			}
		
			fooBean6Beans.add(fooBean6);
		}
		
		return fooBean6Beans;
	}
	
	protected Set<Locale> createLocales(Locale... locales) {
		return new HashSet<>(Arrays.asList(locales));
	}
	
}
