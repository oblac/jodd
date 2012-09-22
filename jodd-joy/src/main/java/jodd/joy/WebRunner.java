// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy;

import jodd.bean.BeanUtil;
import jodd.joy.core.DefaultAppCore;
import jodd.joy.db.AppDao;
import jodd.jtx.JtxTransaction;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransactionMode;
import jodd.madvoc.WebApplication;
import jodd.madvoc.Madvoc;
import jodd.madvoc.component.MadvocConfig;
import jodd.petite.PetiteContainer;
import jodd.util.ReflectUtil;
import jodd.util.StringUtil;

/**
 * Standalone runner for Madvoc web application.
 */
public abstract class WebRunner {

	/**
	 * Web application.
	 */
	protected Madvoc madvoc;
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

		madvoc = new Madvoc();
		madvoc.setWebAppClass(webAppClass);
		madvoc.startNewWebApplication(null);
		
		app = madvoc.getWebApplication();

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

	// ---------------------------------------------------------------- jtx util

	protected static JtxTransactionManager jtxManager;

	/**
	 * Sets transaction manager.
	 */
	public static void setJtxManager(JtxTransactionManager jm) {
		jtxManager = jm;
	}

	/**
	 * Starts new read/write transaction in PROPAGATION_REQUIRED mode.
	 */
	public static JtxTransaction startRwTx() {
		return jtxManager.requestTransaction(new JtxTransactionMode().propagationRequired().readOnly(false));
	}

}