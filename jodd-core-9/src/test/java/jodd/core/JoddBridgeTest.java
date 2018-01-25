package jodd.core;

import java.net.URL;

public class JoddBridgeTest {

	public static void main(String[] args) {
		URL[] urls = JavaBridge.getURLs(JoddBridgeTest.class.getClassLoader());
		for (URL url : urls) {
			System.out.println(url);
		}
		System.out.println("---");
		urls = JavaBridge.getURLs(JavaBridge.class.getClassLoader());
		for (URL url : urls) {
			System.out.println(url);
		}
		System.out.println("---");
		urls = JavaBridge.getURLs(JoddBridgeTest.class);
		for (URL url : urls) {
			System.out.println(url);
		}
	}
}
