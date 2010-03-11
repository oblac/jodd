package jodd.joy;

import jodd.madvoc.WebApplicationStarter;
import jodd.madvoc.WebApplication;
import jodd.jtx.JtxTransaction;
import jodd.jtx.JtxTransactionMode;
import jodd.jtx.JtxTransactionManager;

/**
 * Various helpers for WebApplication runner.
 */
public abstract class WebStarter {

	/**
	 * Starts web application.
	 */
	@SuppressWarnings({"unchecked"})
	public static <W extends WebApplication> W start(Class<W> webAppClass) {
		WebApplicationStarter starter = new WebApplicationStarter();
		starter.setWebAppClass(webAppClass.getName());
		return (W) starter.startNewWebApplication(null);
	}

	protected static JtxTransactionManager jtxManager;
	/**
	 * Sets transaction manager.
	 */
	public static void setJtxManager(JtxTransactionManager jm) {
		jtxManager = jm;
	}

	/**
	 * Starts new transaction.
	 */
	public static JtxTransaction startRwTx() {
		return jtxManager.requestTransaction(new JtxTransactionMode().readOnly(false));
	}
}
