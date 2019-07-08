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

import jodd.db.connection.ConnectionProvider;
import jodd.jtx.JtxTransactionManager;
import jodd.madvoc.WebApp;
import jodd.petite.PetiteContainer;
import jodd.props.Props;
import jodd.proxetta.Proxetta;

/**
 * A simple collection of all Jodd components, available once when Joy is started.
 */
public class JoddJoyRuntime {

	private final Props props;
	private final String appName;
	private final String appDir;
	private final Proxetta proxetta;
	private final PetiteContainer petiteContainer;
	private final boolean databaseEnabled;
	private final ConnectionProvider connectionProvider;
	private final JtxTransactionManager jtxManager;
	private final WebApp webApp;

	public JoddJoyRuntime(
			final String appName,
			final String appDir,
			final Props props,
			final Proxetta proxetta,
			final PetiteContainer petiteContainer,
			final WebApp webApp, final boolean databaseEnabled,
			final ConnectionProvider connectionProvider,
			final JtxTransactionManager jtxManager) {

		this.appName = appName;
		this.appDir = appDir;
		this.props = props;
		this.proxetta = proxetta;
		this.petiteContainer = petiteContainer;
		this.databaseEnabled = databaseEnabled;
		this.connectionProvider = connectionProvider;
		this.jtxManager = jtxManager;
		this.webApp = webApp;
	}

	public JoddJoyRuntime(
			final String appName,
			final String appDir,
			final Props props,
			final Proxetta proxetta,
			final PetiteContainer petiteContainer,
			final WebApp webApp) {

		this(appName, appDir, props, proxetta, petiteContainer, webApp, false, null, null);
	}

	public Props getProps() {
		return props;
	}

	public String getAppName() {
		return appName;
	}

	public String getAppDir() {
		return appDir;
	}

	public Proxetta getProxetta() {
		return proxetta;
	}

	public PetiteContainer getPetiteContainer() {
		return petiteContainer;
	}

	public boolean isDatabaseEnabled() {
		return databaseEnabled;
	}

	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	public JtxTransactionManager getJtxManager() {
		return jtxManager;
	}

	public WebApp getWebApp() {
		return webApp;
	}
}
