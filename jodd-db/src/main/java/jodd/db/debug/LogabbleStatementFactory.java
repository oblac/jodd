package jodd.db.debug;

public class LogabbleStatementFactory {

	public static class Prepared {
		public static final LoggablePreparedStatementFactory FACTORY = new LoggablePreparedStatementFactory();
	}
	public static class Callable {
		public static final LoggableCallableStatementFactory FACTORY = new LoggableCallableStatementFactory();
	}

	public static LoggablePreparedStatementFactory prepared() {
		return Prepared.FACTORY;
	}

	public static LoggableCallableStatementFactory callable() {
		return Callable.FACTORY;
	}
}
