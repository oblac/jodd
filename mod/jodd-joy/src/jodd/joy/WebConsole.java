// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy;

import jodd.bean.BeanUtil;
import jodd.joy.core.DefaultAppCore;
import jodd.joy.db.AppDao;
import jodd.jtx.JtxTransaction;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransactionMode;
import jodd.madvoc.WebApplication;
import jodd.madvoc.WebApplicationStarter;
import jodd.madvoc.component.MadvocConfig;
import jodd.petite.PetiteContainer;
import jodd.util.ReflectUtil;
import jodd.util.StringUtil;

/**
 * Console for Madvoc web application.
 */
public abstract class WebConsole {

	/**
	 * Web application.
	 */
	protected WebApplication app;

	/**
	 * Application core.
	 */
	protected DefaultAppCore appCore;

	/**
	 * Application dao.
	 */
	protected AppDao appDao;

	/**
	 * Petite container used in application.
	 */
	protected PetiteContainer petite;

	/**
	 * Starts the app web application and {@link #run() runs} user code.
	 */
	public void runWebApp(Class<? extends WebApplication> webAppClass) {

		app = start(webAppClass);

		appCore = (DefaultAppCore) BeanUtil.getDeclaredProperty(app, "appCore");

		setJtxManager(appCore.getJtxManager());

		appDao = appCore.getPetite().getBean(AppDao.class);

		petite = appCore.getPetite();

		JtxTransaction tx = startRwTx();
		try {
			System.out.println(StringUtil.repeat('-', 55) + " start");
			System.out.println("\n\n");
			run();
			System.out.println("\n\n");
			System.out.println(StringUtil.repeat('-', 55) + " end");
			tx.commit();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			tx.rollback();
		}

		try {
			ReflectUtil.invokeDeclared(app, "destroy", new Class[] {MadvocConfig.class}, new Object[] {null});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Runs user code without container. At this point everything is up and ready for the usage!
	 */
	public abstract void run();

	// ---------------------------------------------------------------- util

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