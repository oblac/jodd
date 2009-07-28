package examples.speed.io;

import jodd.datetime.JStopWatch;
import jodd.io.StreamUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.OutputStream;

public class StreamCopyBenchmark {

	static JStopWatch swatch = new JStopWatch();

	public static void main(String[] args) throws Exception {
		test1Plain();
		test1Buff();
	}

	// ---------------------------------------------------------------- test #1

	private static byte[] array() {
		byte[] b = new byte[1024000];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) i;
		}
		return b;
	}

	static final int loop1 = 1000000;

	private static void test1Plain() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(array());
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			OutputStream out = new ByteArrayOutputStream();
			StreamUtil.copy(bais, out);
			out.close();
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());		// 16516
	}

	private static void test1Buff() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(array());
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			OutputStream out = new BufferedOutputStream(new ByteArrayOutputStream());
			StreamUtil.copy(bais, out);
			out.close();
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());		// 20781
	}

}
