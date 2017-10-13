// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
import jodd.util.StringUtil;

import java.lang.reflect.Method;

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

		appCore = BeanUtil.declared.getProperty(app, "appCore");

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
			Method destroyMethod = MadvocConfig.class.getDeclaredMethod("destroy");
			destroyMethod.setAccessible(true);
			destroyMethod.invoke(app);
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