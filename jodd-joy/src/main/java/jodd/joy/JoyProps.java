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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Tiny Joy Props kickstarter.
 */
public class JoyProps extends JoyBase implements JoyPropsConfig {
	protected final Supplier<String> nameSupplier;

	protected Props props;

	public JoyProps(final Supplier<String> nameSupplier) {
		this.nameSupplier = nameSupplier;
	}

	// ---------------------------------------------------------------- runtime

	/**
	 * Returns application Props.
	 */
	public Props getProps() {
		return requireStarted(props);
	}

	// ---------------------------------------------------------------- config

	private List<String> propsNamePatterns = new ArrayList<>();
	private List<String> propsProfiles = new ArrayList<>();

	/**
	 * Adds props files or patterns.
	 */
	@Override
	public JoyProps addPropsFile(final String namePattern) {
		requireNotStarted(props);
		this.propsNamePatterns.add(namePattern);
		return this;
	}

	@Override
	public JoyProps addPropsProfiles(final String... profiles) {
		requireNotStarted(props);
		Collections.addAll(propsProfiles, profiles);
		return this;
	}

	// ---------------------------------------------------------------- lifecycle

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

		log.info("PROPS start ----------");

		props = createProps();

		props.loadSystemProperties("sys");
		props.loadEnvironment("env");

		log.debug("Loaded sys&env props: " + props.countTotalProperties() + " properties.");

		props.setActiveProfiles(propsProfiles.toArray(new String[0]));

		// prepare patterns

		final String[] patterns = new String[propsNamePatterns.size() + 1];

		patterns[0] = "/" + nameSupplier.get() + "*.prop*";

		for (int i = 0; i < propsNamePatterns.size(); i++) {
			patterns[i + 1] = propsNamePatterns.get(i);
		}

		log.debug("Loading props from classpath...");

		final long startTime = System.currentTimeMillis();

		props.loadFromClasspath(patterns);

		log.debug("Props scanning completed in " + (System.currentTimeMillis() - startTime) + "ms.");

		log.debug("Total properties: " + props.countTotalProperties());

		log.info("PROPS OK!");
	}

	/**
	 * Creates new {@link Props} with default configuration.
	 * Empty props will be ignored, and missing macros will be
	 * resolved as empty string.
	 */
	protected Props createProps() {
		final Props props = new Props();
		props.setSkipEmptyProps(true);
		props.setIgnoreMissingMacros(true);
		return props;
	}

	@Override
	public void stop() {
		props = null;
	}
}
