package jodd.bridge;

import java.net.URL;

public class JoddBridgeTest {

	public static void main(String[] args) {
		URL[] urls = ClassPathURLs.of(null, JoddBridgeTest.class);
		for (URL url : urls) {
			System.out.println(url);
		}
		System.out.println("---");
		urls = ClassPathURLs.of(null, ClassPathURLs.class);
		for (URL url : urls) {
			System.out.println(url);
		}
	}
}
