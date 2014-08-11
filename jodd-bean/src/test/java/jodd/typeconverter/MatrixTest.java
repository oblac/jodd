package jodd.typeconverter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static jodd.util.ArraysUtil.ints;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MatrixTest {

	private List<Integer> intsList(int... array) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i : array) {
			list.add(Integer.valueOf(i));
		}
		return list;
	}

	@Test
	public void testIntMatrix2() {
		ArrayList<List<Integer>> matrix = new ArrayList<List<Integer>>();

		matrix.add(intsList(1, 2, 3));
		matrix.add(intsList(9, 8, 7));

		int[][] arr = TypeConverterManager.convertType(matrix, int[][].class);

		assertEquals(2, arr.length);

		assertArrayEquals(ints(1,2,3), arr[0]);
		assertArrayEquals(ints(9,8,7), arr[1]);
	}
}