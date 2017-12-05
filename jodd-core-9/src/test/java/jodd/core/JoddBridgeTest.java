package jodd.core;

import java.net.URL;

public class JoddBridgeTest {

	public static void main(String[] args) {
		URL[] urls = JavaBridge.getURLs(JoddBridgeTest.class.getClassLoader());
		for (URL url : urls) {
			System.out.println(url);
		}
	}
}
