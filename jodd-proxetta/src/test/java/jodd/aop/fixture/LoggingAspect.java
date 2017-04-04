package jodd.aop.fixture;

import jodd.aop.Aspect;

import java.lang.reflect.Method;

public class LoggingAspect extends Aspect {

	public static String log = "";

	public LoggingAspect(Object target) {
		super(target);
	}

	@Override
	public boolean before(Object target, Method method, Object[] args) {
		log += "before ";
		return true;
	}

	@Override
	public boolean after(Object target, Method method, Object[] args) {
		log += target.getClass().getName() + '#' + method.getName();
		return true;
	}

	@Override
	public boolean afterException(Object target, Method method, Object[] args, Throwable throwable) {
		return true;
	}
}
