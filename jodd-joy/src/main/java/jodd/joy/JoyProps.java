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

import jodd.props.Props;
import jodd.props.PropsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class JoyProps extends JoyBase {
	protected final Config config;
	protected final Supplier<String> nameSupplier;

	protected Props props;

	public JoyProps(Supplier<String> nameSupplier) {
		this.nameSupplier = nameSupplier;
		this.config = new Config();
	}

	public Props props() {
		return props;
	}

	public Config config() {
		return config;
	}

	public class Config {
		private String propsNamePattern;
		private List<String> propsProfiles = new ArrayList<>();

		public Config setPropsNamePattern(String namePattern) {
			this.propsNamePattern = namePattern;
			return this;
		}

		public Config addPropsProfiles(String... profiles) {
			Collections.addAll(propsProfiles, profiles);
			return this;
		}
	}

	// ---------------------------------------------------------------- start

	/**
	 * Creates and loads application props.
	 * It first loads system properties (registered as <code>sys.*</code>)
	 * and then environment properties (registered as <code>env.*</code>).
	 * Finally, props files are read from the classpath. All properties
	 * are loaded using
	 * <p>
	 * If props have been already loaded, does nothing.
	 */
	@Override
	public void start() {
		initLogger();

		props = createProps();

		props.loadSystemProperties("sys");
		props.loadEnvironment("env");

		props.setActiveProfiles(config.propsProfiles.toArray(new String[0]));

		String namePattern = config.propsNamePattern;

		if (namePattern == null) {
			namePattern = "/" + nameSupplier.get() + "*.prop*";
		}

		log.debug("Loading props from classpath...");

		PropsUtil.loadFromClasspath(props, namePattern);
	}

	/**
	 * Creates new {@link Props} with default configuration.
	 * Empty props will be ignored, and missing macros will be
	 * resolved as empty string.
	 */
	protected Props createProps() {
		Props props = new Props();
		props.setSkipEmptyProps(true);
		props.setIgnoreMissingMacros(true);
		return props;
	}

	@Override
	public void stop() {
	}
}
