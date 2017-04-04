package jodd.aop.fixture;

public class Simple implements Helloable {

	@Override
	public String hello(int a) {
		return "Hello World " + a;
	}
}
