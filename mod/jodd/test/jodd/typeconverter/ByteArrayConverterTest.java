package jodd.typeconverter;

public class ByteArrayConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(ByteArrayConverter.valueOf(null));

		assertEq(arrb((byte)7), ByteArrayConverter.valueOf(Byte.valueOf((byte) 7)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(arrb((byte)1, (byte)7, (byte)3)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(arri(1, 7, 3)));
		assertEq(arrb((byte)1, (byte)7, (byte)3), ByteArrayConverter.valueOf(arrs("1", "7", "3")));
	}

	private void assertEq(byte[] arr1, byte[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i]);
		}
	}

}
